package com.where2meet.ui.base

open class Event {
    object Loading : Event()
    object NotLoading : Event()
    class Error(val throwable: Throwable?) : Event()
}
