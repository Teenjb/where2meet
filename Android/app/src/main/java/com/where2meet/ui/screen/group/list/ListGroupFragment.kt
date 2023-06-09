package com.where2meet.ui.screen.group.list

import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.where2meet.R
import com.where2meet.databinding.FragmentListGroupBinding
import com.where2meet.ui.adapter.GenericLoadStateAdapter
import com.where2meet.ui.adapter.GroupPagingAdapter
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.screen.home.HomeEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat

class ListGroupFragment : BaseFragment(R.layout.fragment_list_group) {
    private val binding by viewBinding<FragmentListGroupBinding>()
    private val viewModel by viewModels<ListGroupViewModel>()

    private lateinit var groupAdapter: GroupPagingAdapter
    private lateinit var searchView: SearchView
    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is HomeEvent.NavigateToDetail -> {
                        toggleLoading(false)
                        val group = event.group
                        logcat { "HomeEvent.NavigateToDetail($group)" }
                        if (!group.hasMood) {
                            navigateTo(
                                ListGroupFragmentDirections.actionListGroupToPickMood(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else if (!group.hasLocation) {
                            navigateTo(
                                ListGroupFragmentDirections.actionListGroupToPickLocation(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else if (!group.hasResult) {
                            navigateTo(
                                ListGroupFragmentDirections.actionListGroupToDetail(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else {
                            navigateTo(
                                ListGroupFragmentDirections.actionListGroupToGroupResult(group.id),
                            )
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
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            toolbar.apply {
                setNavigationOnClickListener { findNavController().popBackStack() }
                // search view
                val searchItem = menu.findItem(R.id.action_search)
                searchView = searchItem.actionView as SearchView
                searchView.queryHint = getString(R.string.hint_list_group_search_view)
                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        toggleLoading(true)
                        val temp = viewModel.query.value
                        if (query == temp) {
                            viewModel.onRefresh()
                        } else {
                            viewModel.searchGroups(query ?: "")
                        }
                        searchView.clearFocus()
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrEmpty()) {
                            viewModel.searchGroups("")
                        }
                        return false
                    }
                })
            }

            swipeRefresh.setOnRefreshListener {
                viewModel.onRefresh()
            }

            setupRecyclerView(rvListGroup)
        }

        // observers
        observeGroups()
        observeQuery()
        observeAdapterLoadState()
    }

    private fun observeGroups() = lifecycleScope.launch {
        toggleLoading(false)
        viewModel.groups
            .flowWithLifecycle(lifecycle)
            .collectLatest(groupAdapter::submitData)
    }

    private fun observeQuery() = lifecycleScope.launch {
        viewModel.query
            .flowWithLifecycle(lifecycle)
            .collectLatest {
                if (it.isBlank()) {
                    binding.lblFilter.text = getString(R.string.lbl_list_group_filter_all)
                } else {
                    binding.lblFilter.text = getString(R.string.lbl_list_group_filter_by_name, it)
                }
            }
    }

    private fun observeAdapterLoadState() = lifecycleScope.launch {
        groupAdapter.loadStateFlow
            .flowWithLifecycle(lifecycle)
            .map { it.refresh }
            .distinctUntilChanged()
            .collect {
                if (it is LoadState.NotLoading) {
                    toggleLoading(false)
                    if (groupAdapter.itemCount < 1) {
                        modifyErrorLayout(
                            "No results found",
                            "We didn't find anything from your search query",
                            "🗑️",
                        )
                        toggleErrorLayout(true)
                    } else {
                        toggleErrorLayout(false)
                    }
                } else if (it is LoadState.Loading) {
                    toggleLoading(true)
                } else if (it is LoadState.Error) {
                    toggleLoading(false)
                    modifyErrorLayout("An error occurred", "Error : ${it.error.message}", "⚠️")
                    snackbar("Error : ${it.error.message}")
                }
            }
    }

    private fun modifyErrorLayout(title: String, message: String, emoji: String) {
        binding.error.apply {
            tvTitle.text = title
            tvMessage.text = message
            tvEmoji.text = emoji
        }
    }

    private fun toggleErrorLayout(flag: Boolean) {
        binding.error.root.isVisible = flag
        binding.rvListGroup.isVisible = !flag
    }

    private fun setupRecyclerView(view: RecyclerView) {
        groupAdapter = GroupPagingAdapter { group ->
            viewModel.navigateToDetail(group.id)
        }
        view.adapter = groupAdapter.withLoadStateHeaderAndFooter(
            header = GenericLoadStateAdapter(groupAdapter::retry),
            footer = GenericLoadStateAdapter(groupAdapter::retry),
        )
        view.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun toggleLoading(flag: Boolean) {
        binding.swipeRefresh.isRefreshing = flag
    }
}
