package com.where2meet.ui.screen.auth.register

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.transition.TransitionInflater
import android.util.Patterns
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.where2meet.R
import com.where2meet.core.domain.model.form.AuthRegister
import com.where2meet.databinding.FragmentRegisterBinding
import com.where2meet.ui.base.BaseFragment
import com.where2meet.ui.base.Event
import com.where2meet.ui.base.forms.FormFieldText
import com.where2meet.ui.base.forms.clearError
import com.where2meet.ui.base.forms.disable
import com.where2meet.ui.base.forms.enable
import com.where2meet.ui.base.forms.validate
import com.where2meet.ui.ext.snackbar
import com.where2meet.ui.ext.toast
import com.where2meet.ui.ext.viewBinding
import com.where2meet.ui.screen.auth.AuthEvent
import com.where2meet.utils.Regexes
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import logcat.logcat
import reactivecircus.flowbinding.android.view.clicks

class RegisterFragment : BaseFragment(R.layout.fragment_register) {
    private val binding by viewBinding<FragmentRegisterBinding>()
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onStart() {
        super.onStart()
        eventJob = viewModel.events
            .onEach { event ->
                when (event) {
                    is AuthEvent.RegisterSuccess -> {
                        toggleLoading(false)
                        toast(
                            getString(R.string.msg_register_success),
                        )
                        navigateTo(RegisterFragmentDirections.actionRegisterToLogin())
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
                            binding.tvLogin,
                        )
                    }
                }
            }.launchIn(lifecycleScope)
    }

    override fun bindView() {
        sharedElementEnterTransition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)

        with(binding) {
            btnRegister.clicks().onEach {
                submit()
            }.launchIn(lifecycleScope)

            tvLogin.apply {
                val span = SpannableString(getString(R.string.lbl_login_now))
                val clickableSpan = object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        navigateTo(RegisterFragmentDirections.actionRegisterToLogin())
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        super.updateDrawState(ds)
                        ds.isUnderlineText = false
                    }
                }
                span.setSpan(
                    clickableSpan,
                    span.indexOf('?') + 2,
                    span.lastIndex + 1,
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
        binding.btnRegister.isEnabled = false

        formFields.disable()
        if (formFields.validate(validateAll = true)) {
            val data = AuthRegister(
                username = fieldUsername.value.toString(),
                email = fieldEmail.value.toString(),
                password = fieldPassword.value.toString(),
            )
            viewModel.onRegister(data)
        }
        formFields.enable()

        binding.btnRegister.isEnabled = true
    }

    // validation
    private val formFields: List<FormFieldText> by lazy {
        listOf(
            fieldUsername,
            fieldEmail,
            fieldPassword,
            fieldConfirmPassword,
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

                    !Regexes.USERNAME_REGEX.matches(value) -> getString(R.string.validation_username)

                    else -> null
                }
            },
        )
    }

    private val fieldEmail by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tfEmail,
            textInputEditText = binding.edEmail,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(
                        R.string.validation_field_required,
                        getString(R.string.lbl_email),
                    )

                    !Patterns.EMAIL_ADDRESS.matcher(value).matches() ->
                        getString(R.string.validation_email)

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

                    !Regexes.PASSWORD_REGEX.matches(value) -> getString(R.string.validation_password)

                    else -> null
                }
            },
        )
    }

    private val fieldConfirmPassword by lazy {
        FormFieldText(
            scope = lifecycleScope,
            textInputLayout = binding.tfConfirmPassword,
            textInputEditText = binding.edConfirmPassword,
            validation = { value ->
                when {
                    value.isNullOrBlank() -> getString(
                        R.string.validation_field_required,
                        getString(R.string.lbl_retype_password),
                    )

                    value != fieldPassword.value -> getString(R.string.validation_confirm_password)

                    else -> null
                }
            },
        )
    }
}
