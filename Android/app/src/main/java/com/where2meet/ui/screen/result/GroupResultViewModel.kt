package com.where2meet.ui.screen.result

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.where2meet.core.domain.model.Group
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupResultViewModel @Inject constructor(
    private val group: GroupRepository,
    handle: SavedStateHandle,
) : BaseViewModel() {
    private val groupId = handle.get<Int>("groupId") ?: -1

    private val mResult = MutableStateFlow<Group?>(null)
    val result = mResult.asStateFlow()

    init {
        fetchResult()
    }

    fun fetchResult() {
        apiJob?.cancel()
        apiJob = viewModelScope.launch {
            Event.Loading.send()
            group.fetchGroup(groupId)
                .catch { Event.Error(it).send() }
                .collect { result ->
                    if (result.isSuccess) {
                        mResult.emit(result.getOrThrow())
                        Event.NotLoading.send()
                    } else if (result.isFailure)
                        Event.Error(result.exceptionOrNull()).send()
                }
        }
    }
}
