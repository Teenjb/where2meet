package com.where2meet.ui.screen.result

import androidx.fragment.app.viewModels
import com.where2meet.R
import com.where2meet.databinding.FragmentResultBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class GroupResultFragment : BaseFragment(R.layout.fragment_result) {
    private val binding by viewBinding<FragmentResultBinding>()
    private val viewModel by viewModels<GroupResultViewModel>()

    override fun bindView() {
        TODO("Not yet implemented")
    }
}
