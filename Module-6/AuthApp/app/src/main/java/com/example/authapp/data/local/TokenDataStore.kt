package com.example.authapp.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

@Singleton
class TokenDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val store = context.dataStore

    val accessTokenFlow: Flow<String?> = store.data.map { it[ACCESS_TOKEN_KEY] }
    val refreshTokenFlow: Flow<String?> = store.data.map { it[REFRESH_TOKEN_KEY] }
    val isLoggedIn: Flow<Boolean> = accessTokenFlow.map { !it.isNullOrBlank() }

    suspend fun getAccessToken(): String? = accessTokenFlow.first()

    suspend fun saveTokens(accessToken: String, refreshToken: String?) {
        store.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = accessToken
            if (refreshToken != null) prefs[REFRESH_TOKEN_KEY] = refreshToken
        }
    }

    suspend fun clear() {
        store.edit { it.clear() }
    }

    private companion object {
        val ACCESS_TOKEN_KEY = stringPreferencesKey("access_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
    }
}
