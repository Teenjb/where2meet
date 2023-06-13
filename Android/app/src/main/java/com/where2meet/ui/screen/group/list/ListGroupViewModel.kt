package com.where2meet.ui.screen.group.list

import androidx.lifecycle.viewModelScope
import com.where2meet.core.data.toParcelable
import com.where2meet.core.domain.repository.AuthRepository
import com.where2meet.core.domain.repository.GroupRepository
import com.where2meet.ui.base.BaseViewModel
import com.where2meet.ui.base.Event
import com.where2meet.ui.screen.home.HomeEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListGroupViewModel @Inject constructor(
    private val group: GroupRepository,
    private val auth: AuthRepository,
) : BaseViewModel() {
    private val mQuery = MutableStateFlow("")
    val query = mQuery.asStateFlow()
    val groups
        get() = mQuery.flatMapLatest {
            group.fetchPagedGroups(it)
        }

    fun searchGroups(query: String) {
        viewModelScope.launch {
            mQuery.emit(query)
        }
    }

    fun onRefresh() {
        val temp = query.value
        searchGroups("")
        searchGroups(temp)
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
                            result.getOrThrow().toParcelable(session.userId),
                        ).send()
                    } else if (result.isFailure) {
                        Event.Error(result.exceptionOrNull()).send()
                    }
                }
        }
    }
}
