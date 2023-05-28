package com.where2meet.ui.screen.auth.login

import androidx.lifecycle.viewModelScope
import com.where2meet.core.domain.model.AuthLogin
import com.where2meet.core.domain.repository.AuthRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import com.where2meet.ui.screen.auth.AuthEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val auth: AuthRepository,
) : BaseViewModel() {
    fun onLogin(
        data: AuthLogin,
    ) {
        fetchJob?.cancel()
        Event.Loading.send()
        fetchJob = viewModelScope.launch {
            auth.login(data)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        AuthEvent.LoginSuccess.send()
                    } else if (result.isFailure) Event.Error(result.exceptionOrNull()).send()
                }
        }
    }
}
