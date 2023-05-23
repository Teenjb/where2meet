package com.where2meet.ui.screen.auth.register

import com.where2meet.R
import com.where2meet.databinding.FragmentRegisterBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class RegisterFragment : BaseFragment(R.layout.fragment_register) {
    private val binding by viewBinding<FragmentRegisterBinding>()

    override fun bindView() {
        with(binding) {
            btnRegister.setOnClickListener {
                navigateTo(RegisterFragmentDirections.actionToLogin())
            }

            tvLogin.setOnClickListener {
                navigateTo(RegisterFragmentDirections.actionToLogin())
            }
        }
    }
}
