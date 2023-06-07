package com.where2meet.ui.screen.group.create.mood

import androidx.core.view.ViewCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.where2meet.R
import com.where2meet.databinding.FragmentPickMoodBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.parcelable.MoodChipData
import com.where2meet.ui.screen.group.create.CreateGroupEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class PickMoodFragment : BaseFragment(R.layout.fragment_pick_mood) {
    private val binding by viewBinding<FragmentPickMoodBinding>()
    private val viewModel by viewModels<PickMoodViewModel>()
    private val args: PickMoodFragmentArgs by navArgs()
    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is CreateGroupEvent.MoodsSubmitted -> {
                        toggleLoading(false)
                        toast(getString(R.string.msg_moods_selected))
                        navigateTo(
                            PickMoodFragmentDirections.actionPickMoodToPickLocation(
                                args.groupId,
                                args.isAdmin
                            )
                        )
                    }

                    is Event.Loading -> {
                        toggleLoading(true)
                    }

                    is Event.NotLoading -> {
                        toggleLoading(false)
                    }

                    is Event.Error -> {
                        toggleLoading(false)
                        logcat { "Error : ${event.throwable?.message}" }
                        snackbar(
                            "Error : ${event.throwable?.message}",
                            binding.btnNext,
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            btnNext.clicks().onEach {
                viewModel.submitMood()
            }.launchIn(lifecycleScope)
        }

        moodsObserver()
        selectedMoodsObserver()
    }

    private fun moodsObserver() = lifecycleScope.launch {
        viewModel.moods
            .flowWithLifecycle(lifecycle)
            .distinctUntilChanged()
            .collectLatest {
                loadChips(it)
            }
    }

    private fun selectedMoodsObserver() = lifecycleScope.launch {
        viewModel.selectedMoods()
            .flowWithLifecycle(lifecycle)
            .collectLatest { list ->
                if (list.isEmpty()) {
                    binding.btnNext.isEnabled = false
                }
            }
    }

    private fun toggleLoading(flag: Boolean) {
        binding.loadingBar.isVisible = flag
    }

    private fun loadChips(data: List<MoodChipData>) {
        logcat { data.toString() }
        data.forEach { moodChip ->
            val chip = Chip(binding.cgMoods.context)
            with(chip) {
                id = ViewCompat.generateViewId()
                text = moodChip.mood.display
                setChipDrawable(
                    ChipDrawable.createFromAttributes(
                        binding.root.context,
                        null,
                        0,
                        R.style.Widget_W2M_MoodChip
                    )
                )
                isChecked = moodChip.isSelected
                setOnCheckedChangeListener { _, checked ->
                    lifecycleScope.launch {
                        val ids = viewModel.selectedMoods().value
                        logcat { "ids is ${ids.map { it.mood.name }} with size ${ids.size}" }
                        if (checked) {
                            if (ids.size < 3) {
                                viewModel.updateSelectedChip(moodChip, true)
                            } else {
                                this@with.isChecked = false
                                logcat { "ids is ${ids.map { it.mood.name }} with size ${ids.size} after checked false" }
                                toast(getString(R.string.err_max_chip_selected, 3))
                            }
                        } else {
                            viewModel.updateSelectedChip(moodChip, false)
                        }
                        val updatedIds = viewModel.selectedMoods().value
                        binding.btnNext.isEnabled = updatedIds.isNotEmpty()
                    }
                }
            }
            binding.cgMoods.addView(chip)
        }
    }
}
