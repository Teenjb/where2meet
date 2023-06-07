package com.where2meet.ui.screen.group.invite

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.where2meet.R
import com.where2meet.databinding.FragmentInviteMemberBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.getColorFromAttr
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class InviteMemberFragment : BaseFragment(R.layout.fragment_invite_member) {
    private val binding by viewBinding<FragmentInviteMemberBinding>()
    private val args by navArgs<InviteMemberFragmentArgs>()

    override fun bindView() {
        with(binding) {
            toolbar.setNavigationOnClickListener { findNavController().popBackStack() }

            val url = getString(R.string.url_invitation, args.groupCode)
            edInviteLink.setText(args.groupCode)
            edInviteLink.setTextColor(requireContext().getColorFromAttr(com.google.android.material.R.attr.colorOnSurface))

            btnCopy.clicks().onEach {
                onCopyLink(url)
            }.launchIn(lifecycleScope)

            btnShare.clicks().onEach {
                onShareLink(url)
            }.launchIn(lifecycleScope)
        }
    }

    private fun onCopyLink(url: String) {
        val clipboardManager =
            requireActivity().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("Invitation Link", url)
        clipboardManager.setPrimaryClip(clipData)
        toast("URL copied to clipboard")
    }

    private fun onShareLink(url: String) {
        Intent(Intent.ACTION_SEND).also {
            it.type = "text/plain"
            it.putExtra(Intent.EXTRA_SUBJECT, "Invitation Link")
            it.putExtra(Intent.EXTRA_TEXT, url)
            startActivity(Intent.createChooser(it, "Share URL"))
        }
    }
}
