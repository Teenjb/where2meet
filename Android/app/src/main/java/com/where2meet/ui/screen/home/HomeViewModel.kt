package com.where2meet.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.where2meet.core.domain.repository.AuthRepository
import com.where2meet.ui.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: AuthRepository,
) : BaseViewModel() {
    val session = auth.session()
    fun onLogout() {
        viewModelScope.launch {
            auth.logout()
        }
    }
}
