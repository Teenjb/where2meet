package com.where2meet.ui.screen.onboarding

import com.where2meet.R
import com.where2meet.databinding.FragmentOnboardingBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class OnboardingFragment : BaseFragment(R.layout.fragment_onboarding) {
    private val binding by viewBinding<FragmentOnboardingBinding>()

    override fun bindView() {
        with(binding) {
            btnLogin.setOnClickListener {
                navigateTo(OnboardingFragmentDirections.actionToLogin())
            }
            btnRegister.setOnClickListener {
                navigateTo(OnboardingFragmentDirections.actionToRegister())
            }
        }
    }
}
