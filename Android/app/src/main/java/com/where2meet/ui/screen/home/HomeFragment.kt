package com.where2meet.ui.screen.home

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.request.ImageRequest
import coil.transform.RoundedCornersTransformation
import com.where2meet.R
import com.where2meet.databinding.FragmentHomeBinding
import com.where2meet.ui.adapter.GroupAdapter
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val binding by viewBinding<FragmentHomeBinding>()
    private val viewModel by viewModels<HomeViewModel>()

    private lateinit var groupAdapter: GroupAdapter

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is HomeEvent.GroupCreated -> {
                        toggleLoading(false)
                        navigateTo(HomeFragmentDirections.actionToPickMood(event.groupId))
                    }

                    is HomeEvent.NavigateToDetail -> {
                        toggleLoading(false)
                        val group = event.group
                        if (!group.hasMood) {
                            navigateTo(HomeFragmentDirections.actionToPickMood(group.id))
                        } else if (!group.hasLocation) {
                            navigateTo(HomeFragmentDirections.actionToPickLocation(group.id))
                        } else if (!group.hasResult) {
                            navigateTo(HomeFragmentDirections.actionToDetail(group.id))
                        } else {
                            navigateTo(HomeFragmentDirections.actionToGroupResult(group.id))
                        }
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
                            binding.bottomAppBar
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            bottomAppBar.apply {
                this.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_logout -> {
                            viewModel.onLogout()
                            toast(
                                getString(R.string.msg_logout),
                            )
                            navigateTo(HomeFragmentDirections.actionToOnboarding())
                            true
                        }

                        else -> true
                    }
                }
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.onRefreshGroups()
            }

            fab.clicks().onEach {
                viewModel.createGroup()
            }.launchIn(lifecycleScope)

            with(home) {
                with(content) {
                    ctaSeeAll.clicks().onEach {
                        navigateTo(HomeFragmentDirections.actionToListGroup())
                    }.launchIn(lifecycleScope)

                    setupRecyclerView(rvHome)
                }
            }
        }

        // observers
        observeSession()
        observeGroups()
    }

    private fun observeSession() = lifecycleScope.launch {
        viewModel.session.collectLatest { session ->
            binding.home.apply {
                tvGreeting.text = getString(R.string.lbl_greeting, session.username)
                ivAvatar.apply {
                    val imgData = ImageRequest.Builder(this.context)
                        .data("https://ui-avatars.com/api/?name=${session.username}&length=1")
                        .target(this)
                        .transformations(RoundedCornersTransformation(16f))
                        .allowHardware(true)
                        .build()
                    imageLoader.enqueue(imgData)
                }
            }
        }
    }

    private fun observeGroups() = lifecycleScope.launch {
        viewModel.groups.collectLatest { groups ->
            groupAdapter.submitList(groups)
        }
    }

    private fun setupRecyclerView(view: RecyclerView) {
        groupAdapter = GroupAdapter { group ->
            viewModel.navigateToDetail(group.id)
        }
        view.adapter = groupAdapter
        view.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun toggleLoading(flag: Boolean) {
        binding.swipeRefresh.isRefreshing = flag
    }
}
