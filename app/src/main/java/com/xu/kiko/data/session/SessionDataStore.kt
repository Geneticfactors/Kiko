package com.xu.kiko.data.session

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.sessionDataStore by preferencesDataStore(
    name = "kiko_session"
)

class SessionDataStore(
    context: Context
) : SessionStore {
    private val dataStore = context.applicationContext.sessionDataStore

    override fun observeCurrentUserId(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[CURRENT_USER_ID]
        }
    }

    override suspend fun setCurrentUserId(userId: String) {
        dataStore.edit { preferences ->
            preferences[CURRENT_USER_ID] = userId
        }
    }

    override suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(CURRENT_USER_ID)
        }
    }

    private companion object {
        val CURRENT_USER_ID = stringPreferencesKey("current_user_id")
    }
}
