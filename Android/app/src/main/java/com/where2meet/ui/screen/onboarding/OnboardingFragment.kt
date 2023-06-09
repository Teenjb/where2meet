package com.where2meet.ui.screen.onboarding

import androidx.navigation.fragment.FragmentNavigatorExtras
import com.where2meet.R
import com.where2meet.databinding.FragmentOnBoardingBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class OnBoardingFragment : BaseFragment(R.layout.fragment_on_boarding) {
    private val binding by viewBinding<FragmentOnBoardingBinding>()

    override fun bindView() {
        val navigatorExtras = FragmentNavigatorExtras(
            binding.hero to "hero",
            binding.ivLogo to "logo",
        )
        with(binding) {
            btnLogin.setOnClickListener {
                navigateTo(
                    OnBoardingFragmentDirections.actionOnboardingToLogin(),
                    navigatorExtras,
                )
            }
            btnRegister.setOnClickListener {
                navigateTo(
                    OnBoardingFragmentDirections.actionOnboardingToRegister(),
                    navigatorExtras,
                )
            }
        }
    }
}
