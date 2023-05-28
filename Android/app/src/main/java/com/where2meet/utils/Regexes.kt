package com.where2meet.utils

object Regexes {
    val usernameRegex = "^[a-zA-Z0-9]+\$".toRegex()
    val passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$".toRegex()
}
