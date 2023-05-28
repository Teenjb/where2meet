package com.where2meet.ui.screen.auth.login

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.where2meet.R
import com.where2meet.core.domain.model.AuthLogin
import com.where2meet.databinding.FragmentLoginBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.base.forms.FormFieldText
import com.where2meet.ui.base.forms.clearError
import com.where2meet.ui.base.forms.disable
import com.where2meet.ui.base.forms.enable
import com.where2meet.ui.base.forms.validate
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.screen.auth.AuthEvent
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class LoginFragment : BaseFragment(R.layout.fragment_login) {
    private val binding by viewBinding<FragmentLoginBinding>()
    private val viewModel by viewModels<LoginViewModel>()

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is AuthEvent.LoginSuccess -> {
                        toggleLoading(false)
                        snackbar(
                            getString(R.string.msg_login_success),
                            binding.tvRegister,
                        )
                        navigateTo(LoginFragmentDirections.actionToHome())
                    }

                    is Event.Loading -> {
                        toggleLoading(true)
                        binding.loadingBar.isVisible = true
                    }

                    is Event.Error -> {
                        toggleLoading(false)
                        logcat { "Error : ${event.throwable?.message}" }
                        snackbar(
                            "Error : ${event.throwable?.message}",
                            binding.tvRegister,
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        with(binding) {
            btnLogin.clicks().onEach {
                submit()
            }.launchIn(lifecycleScope)

            tvRegister.apply {
                val span = SpannableString(getString(R.string.lbl_register_now))
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        navigateTo(LoginFragmentDirections.actionToRegister())
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                span.setSpan(
                    clickableSpan,
                    span.indexOf('?') + 2,
                    span.lastIndex,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE,
                )
                text = span
                movementMethod = LinkMovementMethod.getInstance()
                highlightColor = Color.TRANSPARENT
            }
        }
        formFields.clearError()
    }

    private fun toggleLoading(flag: Boolean) {
        binding.loadingBar.isVisible = flag
    }

    private fun submit() = lifecycleScope.launch {
        binding.btnLogin.isEnabled = false

        formFields.disable()
        if (formFields.validate(validateAll = true)) {
            val data = AuthLogin(
                username = fieldUsername.value.toString(),
                password = fieldPassword.value.toString(),
            )
            viewModel.onLogin(data)
        }
        formFields.enable()

        binding.btnLogin.isEnabled = true
    }

    // validation
    private val formFields: List<FormFieldText> by lazy {
        listOf(
            fieldUsername,
            fieldPassword,
        )
    }

    private val fieldUsername by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tfUsername,
            textInputEditText = binding.edUsername,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(
                        R.string.validation_field_required,
                        getString(R.string.lbl_username),
                    )

                    else -> null
                }
            },
        )
    }

    private val fieldPassword by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tfPassword,
            textInputEditText = binding.edPassword,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(
                        R.string.validation_field_required,
                        getString(R.string.lbl_password),
                    )

                    else -> null
                }
            },
        )
    }
}
