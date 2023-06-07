package com.where2meet.ui.screen.group.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.where2meet.core.data.preference.DataStoreManager
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.model.form.DeleteMember
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import com.where2meet.ui.screen.group.GroupEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailGroupViewModel @Inject constructor(
    private val group: GroupRepository,
    preference: DataStoreManager,
    handle: SavedStateHandle
) : BaseViewModel() {
    private val groupId = handle.get<Int>("groupId") ?: -1

    private val mDetail = MutableStateFlow<Group?>(null)
    val detail = mDetail.asStateFlow()

    val userId = preference.session.flatMapLatest {
        flow {
            emit(it.userId)
        }
    }

    init {
        fetchDetail()
    }

    fun fetchDetail() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.fetchGroup(groupId)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        mDetail.emit(result.getOrThrow())
                        Event.NotLoading.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun onDeleteGroup() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.deleteGroup(groupId)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        GroupEvent.GroupDeleted.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun onDeleteGroupMember(memberId: Int) {
        val form = DeleteMember(groupId, memberId)
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.deleteMember(form)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        GroupEvent.MemberRemoved.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }

    fun onGenerateRecommendation() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.generateRecommendation(groupId)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        GroupEvent.RecommendationGenerated(result.getOrThrow().id).send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }
}
