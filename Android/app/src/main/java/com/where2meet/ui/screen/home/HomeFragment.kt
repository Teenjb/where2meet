package com.where2meet.ui.screen.home

import com.where2meet.R
import com.where2meet.databinding.FragmentHomeBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class HomeFragment : BaseFragment(R.layout.fragment_home) {
    private val binding by viewBinding<FragmentHomeBinding>()
    override fun bindView() {
        with(binding) {
            bottomAppBar.apply {
                this.setOnMenuItemClickListener { menuItem ->
                    when (menuItem.itemId) {
                        R.id.action_logout -> {
                            navigateTo(HomeFragmentDirections.actionToOnboarding())
                            true
                        }

                        else -> true
                    }
                }
            }
        }
    }
}
