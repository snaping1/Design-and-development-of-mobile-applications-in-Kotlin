package com.example.nobellaureatesclient.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.nobellaureatesclient.domain.model.AuthSession
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenStorage @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    private val dataStore = context.authDataStore

    val session: Flow<AuthSession?> = dataStore.data.map { prefs ->
        val token = prefs[KEY_TOKEN]?.takeIf { it.isNotBlank() } ?: return@map null
        val username = prefs[KEY_USERNAME].orEmpty()
        val expiresAt = prefs[KEY_EXPIRES_AT] ?: 0L
        AuthSession(token = token, username = username, expiresAtMillis = expiresAt)
    }

    suspend fun currentSession(): AuthSession? = session.first()

    suspend fun currentToken(): String? = currentSession()?.takeIf { it.isValid() }?.token

    suspend fun save(token: String, username: String, expiresInMillis: Long) {
        val expiresAt = System.currentTimeMillis() + expiresInMillis
        dataStore.edit { prefs ->
            prefs[KEY_TOKEN] = token
            prefs[KEY_USERNAME] = username
            prefs[KEY_EXPIRES_AT] = expiresAt
        }
    }

    suspend fun clear() {
        dataStore.edit { it.clear() }
    }

    private companion object {
        val KEY_TOKEN = stringPreferencesKey("token")
        val KEY_USERNAME = stringPreferencesKey("username")
        val KEY_EXPIRES_AT = longPreferencesKey("expires_at")
    }
}
