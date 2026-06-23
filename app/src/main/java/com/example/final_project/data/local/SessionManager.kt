package com.example.final_project.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.final_project.data.remote.dto.LoginData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "auth_session"
)

class SessionManager(private val context: Context) {

    private object Keys {
        val TOKEN = stringPreferencesKey("token")
        val USER_ID = longPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_ROLE = stringPreferencesKey("user_role")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val USER_AVATAR = stringPreferencesKey("user_avatar")
    }

    companion object {
        @Volatile
        private var tokenCache: String? = null
    }

    val isLoggedIn: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        !prefs[Keys.TOKEN].isNullOrBlank()
    }

    fun getToken(): String? = tokenCache

    suspend fun hydrateToken() {
        if (tokenCache == null) {
            val prefs = context.sessionDataStore.data.first()
            tokenCache = prefs[Keys.TOKEN]
        }
    }

    suspend fun saveLogin(data: LoginData) {
        tokenCache = data.token
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.TOKEN] = data.token
            prefs[Keys.USER_ID] = data.id
            prefs[Keys.USER_NAME] = data.name
            prefs[Keys.USER_EMAIL] = data.email
            prefs[Keys.USER_ROLE] = data.role
        }
    }

    suspend fun saveProfile(name: String, phone: String?, avatarUrl: String?) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.USER_NAME] = name
            if (phone != null) {
                prefs[Keys.USER_PHONE] = phone
            }
            if (avatarUrl != null) {
                prefs[Keys.USER_AVATAR] = avatarUrl
            }
        }
    }

    suspend fun clearSession() {
        tokenCache = null
        context.sessionDataStore.edit { it.clear() }
    }
}
