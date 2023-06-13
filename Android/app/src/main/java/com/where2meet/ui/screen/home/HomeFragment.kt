package com.where2meet.ui.screen.home

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.request.ImageRequest
import com.afollestad.materialdialogs.MaterialDialog
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
    private val args by navArgs<HomeFragmentArgs>()

    private lateinit var groupAdapter: GroupAdapter

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is HomeEvent.GroupCreated -> {
                        viewModel.onRefreshGroups()
                        toggleLoading(false)
                        navigateTo(
                            HomeFragmentDirections.actionHomeToPickMood(
                                event.groupId,
                                true,
                            ),
                        )
                    }

                    is HomeEvent.GroupJoined -> {
                        viewModel.onRefreshGroups()
                        toggleLoading(false)
                        navigateTo(
                            HomeFragmentDirections.actionHomeToPickMood(
                                event.groupId,
                                false,
                            ),
                        )
                    }

                    is HomeEvent.NavigateToDetail -> {
                        toggleLoading(false)
                        val group = event.group
                        logcat { "HomeEvent.NavigateToDetail($group)" }
                        if (!group.hasMood) {
                            navigateTo(
                                HomeFragmentDirections.actionHomeToPickMood(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else if (!group.hasLocation) {
                            navigateTo(
                                HomeFragmentDirections.actionHomeToPickLocation(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else if (!group.hasResult) {
                            navigateTo(
                                HomeFragmentDirections.actionHomeToDetail(
                                    group.id,
                                    group.isAdmin,
                                ),
                            )
                        } else {
                            navigateTo(
                                HomeFragmentDirections.actionHomeToGroupResult(group.id),
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
                            binding.bottomAppBar,
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onRefreshGroups()
    }

    override fun bindView() {
        if (args.invitationCode.isNotBlank() && !viewModel.showedInvitation.value) {
            showInvitationDialog()
        }
        with(binding) {
            bottomAppBar.apply {
                this.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_logout -> {
                            viewModel.onLogout()
                            toast(
                                getString(R.string.msg_logout),
                            )
                            navigateTo(HomeFragmentDirections.actionHomeToOnboarding())
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
                        navigateTo(HomeFragmentDirections.actionHomeToListGroup())
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
        viewModel.session.flowWithLifecycle(lifecycle).collectLatest { session ->
            if (session.token.isBlank()) {
                navigateTo(HomeFragmentDirections.actionHomeToOnboarding())
            }
            binding.home.apply {
                tvGreeting.text = getString(R.string.lbl_greeting, session.username)
                ivAvatar.apply {
                    val imgData = ImageRequest.Builder(this.context)
                        .data("https://ui-avatars.com/api/?name=${session.username}&length=1")
                        .target(this)
                        .allowHardware(true)
                        .build()
                    imageLoader.enqueue(imgData)
                }
            }
        }
    }

    private fun observeGroups() = lifecycleScope.launch {
        viewModel.groups.flowWithLifecycle(lifecycle).collectLatest { groups ->
            if (groups.isEmpty()) {
                modifyErrorLayout(
                    "No results found",
                    "We didn't find anything from your search query",
                    "🗑️",
                )
                toggleErrorLayout(true)
            } else {
                toggleErrorLayout(false)
                groupAdapter.submitList(groups)
            }
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

    private fun showInvitationDialog() {
        viewModel.onShowedInvitation()
        MaterialDialog(requireContext()).show {
            title(R.string.lbl_accept_invitation)
            message(R.string.lbl_invite_confirmation_desc)
            positiveButton(R.string.lbl_accept) {
                viewModel.acceptInvitation(args.invitationCode)
            }
            negativeButton(R.string.lbl_reject)
        }
    }

    private fun modifyErrorLayout(title: String, message: String, emoji: String) {
        with(binding.home.error) {
            tvTitle.text = title
            tvMessage.text = message
            tvEmoji.text = emoji
        }
    }

    private fun toggleErrorLayout(flag: Boolean) {
        with(binding.home) {
            error.root.isVisible = flag
            content.lblSeeMore.isVisible = !flag
            content.rvHome.isVisible = !flag
        }
    }
}
