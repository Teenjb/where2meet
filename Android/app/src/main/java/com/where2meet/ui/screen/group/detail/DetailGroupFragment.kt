package com.where2meet.ui.screen.group.detail

import android.annotation.SuppressLint
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
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
                    is GroupEvent.GroupUpdated -> {
                        toast("Group updated!")
                        viewModel.fetchDetail()
                    }

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
        viewModel.detail
            .flowWithLifecycle(lifecycle)
            .collectLatest {
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
                formatIsoDateString(data.createdAt, "MMMM dd, yyyy"),
            )

            btnInvite.isEnabled = true
            btnInvite.clicks().onEach {
                navigateTo(DetailGroupFragmentDirections.actionDetailToInviteMember(data.code))
            }.launchIn(lifecycleScope)

            ivEdit.clicks().onEach {
                showUpdateGroupDialog()
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
                        args.isAdmin,
                    ),
                )
            },
            { userGroup ->
                showDeleteMemberDialog(userGroup.user)
            },
        )
        view.adapter = memberAdapter
        view.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun showDeleteGroupDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_delete_confirmation_title)
            message(R.string.dialog_delete_group_message)
            positiveButton(R.string.dialog_btn_yes) {
                viewModel.onDeleteGroup()
            }
            negativeButton(R.string.dialog_btn_no)
        }
    }

    private fun showDeleteMemberDialog(user: User) {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_delete_confirmation_title)
            message(R.string.dialog_delete_member_message, user.username)
            positiveButton(R.string.dialog_btn_yes) {
                viewModel.onDeleteGroupMember(user.id)
            }
            negativeButton(R.string.dialog_btn_no)
        }
    }

    @SuppressLint("CheckResult")
    private fun showUpdateGroupDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_update_group_title)
            input { _, text ->
                viewModel.onUpdateGroup(text.toString())
            }
            positiveButton(R.string.dialog_btn_submit)
            negativeButton(R.string.dialog_btn_cancel)
        }
    }

    private fun showGenerateConfirmationDialog() {
        MaterialDialog(requireContext()).show {
            title(R.string.dialog_generate_confirmation_title)
            message(R.string.dialog_generate_confirmation_message)
            positiveButton(R.string.dialog_btn_yes) {
                viewModel.onGenerateRecommendation()
            }
            negativeButton(R.string.dialog_btn_no) { }
        }
    }
}
