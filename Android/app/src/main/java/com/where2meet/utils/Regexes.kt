package com.where2meet.utils

object Regexes {
    val USERNAME_REGEX = "^[a-zA-Z0-9]+\$".toRegex()
    val PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}\$".toRegex()
}
