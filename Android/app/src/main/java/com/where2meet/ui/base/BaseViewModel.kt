package com.where2meet.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.where2meet.ui.base.BaseEvent.ShowErrorMessage
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

abstract class BaseViewModel : ViewModel() {
    private val eventChannel = Channel<BaseEvent>(Channel.BUFFERED)
    val events: Flow<BaseEvent> = eventChannel.receiveAsFlow()

    protected suspend fun sendNewEvent(event: BaseEvent) {
        eventChannel.send(event)
    }

    protected suspend fun showErrorMessage(message: String?) {
        eventChannel.send(ShowErrorMessage(message ?: ""))
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}
