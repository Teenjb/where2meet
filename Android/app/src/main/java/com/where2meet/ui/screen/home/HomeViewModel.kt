package com.where2meet.ui.screen.home

import androidx.lifecycle.viewModelScope
import com.where2meet.core.data.toParcelable
import com.where2meet.core.domain.model.MiniGroup
import com.where2meet.core.domain.model.form.JoinGroup
import com.where2meet.core.domain.repository.AuthRepository
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val auth: AuthRepository,
    private val group: GroupRepository,
) : BaseViewModel() {
    val session = auth.session()

    private val mShowedInvitation = MutableStateFlow(false)
    val showedInvitation = mShowedInvitation.asStateFlow()

    private val mGroups = MutableStateFlow(emptyList<MiniGroup>())
    val groups = mGroups.asStateFlow()

    init {
        onRefreshGroups()
    }

    fun onRefreshGroups() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.fetchGroups(5)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        mGroups.emit(result.getOrThrow())
                        Event.NotLoading.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun onLogout() {
        viewModelScope.launch {
            auth.logout()
        }
    }

    fun createGroup() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.createGroup()
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        HomeEvent.GroupCreated(result.getOrThrow().id).send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun acceptInvitation(code: String) {
        val form = JoinGroup(code)
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.joinGroup(form)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        HomeEvent.GroupJoined(result.getOrThrow().id).send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun onShowedInvitation() {
        viewModelScope.launch {
            mShowedInvitation.value = true
        }
    }

    fun navigateToDetail(groupId: Int) {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            val session = auth.session().first()
            Event.Loading.send()
            group.fetchGroup(groupId)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        HomeEvent.NavigateToDetail(
                            result.getOrThrow().toParcelable(session.userId)
                        ).send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }
}
