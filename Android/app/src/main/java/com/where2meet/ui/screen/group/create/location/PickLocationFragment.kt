package com.where2meet.ui.screen.group.create.location

import androidx.fragment.app.viewModels
import com.where2meet.R
import com.where2meet.databinding.FragmentPickLocationBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class PickLocationFragment : BaseFragment(R.layout.fragment_pick_location) {
    private val binding by viewBinding<FragmentPickLocationBinding>()
    private val viewModel by viewModels<PickLocationViewModel>()

    override fun bindView() {
        TODO("Not yet implemented")
    }
}
