package com.xu.kiko.data.profile

import android.content.Context
import android.net.Uri
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

private val Context.profileDataStore by preferencesDataStore(
    name = "kiko_profile"
)

class ProfilePreferencesStore(
    context: Context
) {
    private val appContext = context.applicationContext
    private val dataStore = appContext.profileDataStore

    fun observeAvatarImagePath(userId: String): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[avatarImagePathKey(userId)]
        }
    }

    suspend fun saveAvatarImage(
        userId: String,
        sourceUri: String
    ) {
        val avatarFile = copyAvatarToInternalStorage(
            userId = userId,
            sourceUri = sourceUri
        )
        dataStore.edit { preferences ->
            preferences[avatarImagePathKey(userId)] =
                avatarFile.absolutePath
        }
    }

    private suspend fun copyAvatarToInternalStorage(
        userId: String,
        sourceUri: String
    ): File = withContext(Dispatchers.IO) {
        val avatarDir = File(appContext.filesDir, AVATAR_DIR_NAME)
        avatarDir.mkdirs()

        val avatarFile = File(
            avatarDir,
            "${safeFileName(userId)}.jpg"
        )
        appContext.contentResolver
            .openInputStream(Uri.parse(sourceUri))
            .use { input ->
                requireNotNull(input) {
                    "Unable to open avatar image"
                }
                avatarFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        avatarFile
    }

    private fun avatarImagePathKey(userId: String) =
        stringPreferencesKey("avatar_image_path_$userId")

    private fun safeFileName(value: String): String {
        return value.replace(Regex("[^A-Za-z0-9_-]"), "_")
    }

    private companion object {
        const val AVATAR_DIR_NAME = "profile_avatars"
    }
}
