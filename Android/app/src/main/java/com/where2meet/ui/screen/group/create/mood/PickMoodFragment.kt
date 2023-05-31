package com.where2meet.ui.screen.group.create.mood

import androidx.fragment.app.viewModels
import com.where2meet.R
import com.where2meet.databinding.FragmentPickMoodBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class PickMoodFragment : BaseFragment(R.layout.fragment_pick_mood) {
    private val binding by viewBinding<FragmentPickMoodBinding>()
    private val viewModel by viewModels<PickMoodViewModel>()

    override fun bindView() {
        TODO("Not yet implemented")
    }
}
