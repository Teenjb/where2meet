package com.where2meet.ui.screen.auth

import com.where2meet.ui.base.Event

sealed class AuthEvent : Event() {
    object LoginSuccess : AuthEvent()
    object RegisterSuccess : AuthEvent()
}
