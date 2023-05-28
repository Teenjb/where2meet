package com.where2meet.core.data.preference

import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val THEME_KEY = stringPreferencesKey("theme")
    val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
    val SESSION_USERNAME_KEY = stringPreferencesKey("session_username")
}
