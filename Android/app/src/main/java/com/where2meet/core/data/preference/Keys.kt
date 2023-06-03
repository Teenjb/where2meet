package com.where2meet.core.data.preference

import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Keys {
    val THEME_KEY = stringPreferencesKey("theme")
    val SESSION_TOKEN_KEY = stringPreferencesKey("session_token")
    val SESSION_USERID_KEY = intPreferencesKey("session_userid")
    val SESSION_USERNAME_KEY = stringPreferencesKey("session_username")
}
