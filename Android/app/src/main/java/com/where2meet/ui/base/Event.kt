package com.where2meet.ui.base

open class Event {
    object Loading : Event()
    class Success<T>(val data: T) : Event()
    class Digest(val message: String) : Event()
    class Error(val throwable: Throwable?) : Event()
}
