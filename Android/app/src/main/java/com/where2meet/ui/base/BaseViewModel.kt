package com.where2meet.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel : ViewModel() {

    private val mutableEvents = Channel<Event>()
    val events: Flow<Event> = mutableEvents.receiveAsFlow()

    protected var fetchJob: Job? = null

    protected fun Event.send() {
        viewModelScope.launch { mutableEvents.send(this@send) }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
