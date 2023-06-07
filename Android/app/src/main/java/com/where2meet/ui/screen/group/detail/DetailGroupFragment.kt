package com.where2meet.ui.screen.group.detail

import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.where2meet.R
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.User
import com.where2meet.databinding.FragmentDetailGroupBinding
import com.where2meet.ui.adapter.UserGroupAdapter
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.ext.formatIsoDateString
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.screen.group.GroupEvent
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class DetailGroupFragment : BaseFragment(R.layout.fragment_detail_group) {
    private val binding by viewBinding<FragmentDetailGroupBinding>()
    private val viewModel by viewModels<DetailGroupViewModel>()
    private val args by navArgs<DetailGroupFragmentArgs>()

    private lateinit var memberAdapter: UserGroupAdapter

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is GroupEvent.GroupDeleted -> {
                        toast("Group deleted!")
                        findNavController().popBackStack()
                    }

                    is GroupEvent.MemberRemoved -> {
                        toast("Member removed!")
                        viewModel.fetchDetail()
                    }

                    is GroupEvent.RecommendationGenerated -> {
                        navigateTo(DetailGroupFragmentDirections.actionDetailToGroupResult(event.groupId))
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
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            btnInvite.isEnabled = false

            swipeRefresh.setOnRefreshListener {
                viewModel.fetchDetail()
            }
        }
        configureGroupAdminPrivilege(args.isAdmin)

        // observers
        detailObserver()
    }

    private fun detailObserver() = lifecycleScope.launch {
        viewModel.detail.collectLatest {
            if (it == null) return@collectLatest
            showDetail(it)
        }
    }

    private fun showDetail(data: Group) {
        val owner = data.users.find { it.user.id == data.adminId }?.user ?: return
        with(binding) {
            tvName.text = data.name
            tvCreatedAt.text = getString(
                R.string.lbl_group_created_at,
                owner.username,
                formatIsoDateString(data.createdAt, "MMMM dd, yyyy")
            )

            btnInvite.isEnabled = true
            btnInvite.clicks().onEach {
                navigateTo(DetailGroupFragmentDirections.actionDetailToInviteMember(data.code))
            }.launchIn(lifecycleScope)

            btnGenerate.clicks().onEach {
                showGenerateConfirmationDialog()
            }.launchIn(lifecycleScope)

            toolbar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_delete -> {
                        showDeleteGroupDialog()
                        true
                    }

                    else -> {
                        true
                    }
                }
            }

            val userId = runBlocking { viewModel.userId.first() }
            setupRecyclerView(rvMember, userId, data.adminId)
            memberAdapter.submitList(data.users)
        }
    }

    private fun toggleLoading(flag: Boolean) {
        binding.swipeRefresh.isRefreshing = flag
    }

    private fun configureGroupAdminPrivilege(isAdmin: Boolean) {
        with(binding) {
            btnGenerate.isVisible = isAdmin
            lblGenerateDisabled.isVisible = !isAdmin
            ivEdit.isVisible = isAdmin
            with(toolbar.menu) {
                val deleteItem = findItem(R.id.action_delete)
                deleteItem.setVisible(isAdmin)
            }
        }
    }

    private fun setupRecyclerView(view: RecyclerView, userId: Int, adminId: Int) {
        memberAdapter = UserGroupAdapter(
            imageLoader,
            userId,
            adminId,
            args.isAdmin,
            { _ ->
                navigateTo(
                    DetailGroupFragmentDirections.actionDetailToPickMood(
                        args.groupId,
                        args.isAdmin
                    )
                )
            }, { userGroup ->
                showDeleteMemberDialog(userGroup.user)
            }
        )
        view.adapter = memberAdapter
        view.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showDeleteGroupDialog() {
        MaterialDialog(requireContext()).show {
            title(text = "Delete Confirmation")
            message(text = "Are you sure you want to delete this group?")
            positiveButton(text = "Yes") {
                viewModel.onDeleteGroup()
            }
            negativeButton(text = "No") { }
        }
    }

    private fun showDeleteMemberDialog(user: User) {
        MaterialDialog(requireContext()).show {
            title(text = "Delete Confirmation")
            message(text = "Are you sure you want to remove ${user.username} from this group?")
            positiveButton(text = "Yes") {
                viewModel.onDeleteGroupMember(user.id)
            }
            negativeButton(text = "No") { }
        }
    }

    private fun showGenerateConfirmationDialog() {
        MaterialDialog(requireContext()).show {
            title(text = "Confirmation")
            message(text = "After generate, this group will be locked and couldn't be edited anymore. Are you sure you want to generate recommendation for this group?")
            positiveButton(text = "Yes") {
                viewModel.onGenerateRecommendation()
            }
            negativeButton(text = "No") { }
        }
    }
}
