package com.where2meet.ui.screen.auth.login

import com.where2meet.R
import com.where2meet.databinding.FragmentLoginBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.ext.viewBinding

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private val binding by viewBinding<FragmentLoginBinding>()

    override fun bindView() {
        with(binding) {
            btnLogin.setOnClickListener {
                navigateTo(LoginFragmentDirections.actionToHome())
            }

            tvRegister.setOnClickListener {
                navigateTo(LoginFragmentDirections.actionToRegister())
            }
        }
    }
}
