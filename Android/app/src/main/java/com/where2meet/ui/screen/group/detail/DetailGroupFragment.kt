package com.where2meet.ui.screen.group.detail

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.where2meet.R
import com.where2meet.databinding.FragmentDetailGroupBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import reactivecircus.flowbinding.android.view.clicks

class DetailGroupFragment : BaseFragment(R.layout.fragment_detail_group) {
    private val binding by viewBinding<FragmentDetailGroupBinding>()
    private val viewModel by viewModels<DetailGroupViewModel>()

    override fun bindView() {
        with(binding) {
            btnInvite.clicks().onEach {
                navigateTo(DetailGroupFragmentDirections.actionToInviteMember("abdce"))
            }.launchIn(lifecycleScope)
        }
    }
}
