package com.where2meet.core.data.preference

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.where2meet.core.domain.model.Session
import com.where2meet.ui.ext.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("prefs")

class DataStoreManager(
    appContext: Context,
) {
    private val prefsDataStore = appContext.dataStore

    suspend fun setString(key: Preferences.Key<String>, value: String) {
        prefsDataStore.edit { prefs -> prefs[key] = value }
    }

    fun getString(key: Preferences.Key<String>, default: String = ""): Flow<String?> =
        prefsDataStore.data.map { it[key] ?: default }

    // theme
    val theme: Flow<String>
        get() = prefsDataStore.data.map { prefs ->
            prefs[Keys.THEME_KEY] ?: AppTheme.LIGHT.name
        }

    // session
    val session: Flow<Session>
        get() = prefsDataStore.data.map { prefs ->
            Session(
                token = prefs[Keys.SESSION_TOKEN_KEY] ?: "",
                username = prefs[Keys.SESSION_USERNAME_KEY] ?: "",
            )
        }

    suspend fun addSession(data: Session) {
        prefsDataStore.edit { prefs ->
            prefs[Keys.SESSION_USERNAME_KEY] = data.username
            prefs[Keys.SESSION_TOKEN_KEY] = "Bearer $data.token"
        }
    }

    suspend fun clearSession() {
        prefsDataStore.edit { prefs ->
            prefs[Keys.SESSION_TOKEN_KEY] = ""
            prefs[Keys.SESSION_USERNAME_KEY] = ""
        }
    }

    val isLoggedIn: Flow<Boolean>
        get() = prefsDataStore.data.map { prefs ->
            (prefs[Keys.SESSION_TOKEN_KEY] ?: "").isNotEmpty()
        }
}
