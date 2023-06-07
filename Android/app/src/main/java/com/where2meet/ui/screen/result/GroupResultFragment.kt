package com.where2meet.ui.screen.result

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.where2meet.R
import com.where2meet.core.domain.model.Group
import com.where2meet.databinding.FragmentResultBinding
import com.where2meet.ui.adapter.ResultAdapter
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.formatIsoDateString
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

class GroupResultFragment : BaseFragment(R.layout.fragment_result) {
    private val binding by viewBinding<FragmentResultBinding>()
    private val viewModel by viewModels<GroupResultViewModel>()

    private lateinit var resultAdapter: ResultAdapter

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
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
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            swipeRefresh.setOnRefreshListener {
                viewModel.fetchResult()
            }
        }

        // observers
        resultObserver()
    }

    private fun resultObserver() = lifecycleScope.launch {
        viewModel.result.collectLatest {
            if (it == null) return@collectLatest
            showResult(it)
        }
    }

    private fun showResult(data: Group) {
        with(binding) {
            tvName.text = data.name
            tvGeneratedAt.text = getString(
                R.string.lbl_group_generated_at,
                formatIsoDateString(data.createdAt, "MMMM dd, yyyy")
            )

            setupRecyclerView(rvResult)
            resultAdapter.submitList(data.result)
        }
    }

    private fun toggleLoading(flag: Boolean) {
        binding.swipeRefresh.isRefreshing = flag
    }

    private fun setupRecyclerView(view: RecyclerView) {
        resultAdapter = ResultAdapter(
            imageLoader
        ) { result ->
            logcat { "result -> $result" }
        }
        view.adapter = resultAdapter
        view.layoutManager = LinearLayoutManager(requireContext())
    }
}
