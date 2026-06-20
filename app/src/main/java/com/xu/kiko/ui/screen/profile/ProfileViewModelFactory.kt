package com.xu.kiko.ui.screen.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.data.profile.ProfilePreferencesStore
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.FocusSessionRepository

class ProfileViewModelFactory(
    private val authRepository: AuthRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val profilePreferencesStore: ProfilePreferencesStore,
    private val currentUserId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                authRepository = authRepository,
                focusSessionRepository = focusSessionRepository,
                profilePreferencesStore = profilePreferencesStore,
                currentUserId = currentUserId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        fun fromContext(
            context: Context,
            currentUserId: String
        ): ProfileViewModelFactory {
            return ProfileViewModelFactory(
                authRepository = AppDependencies.authRepository(context),
                focusSessionRepository =
                    AppDependencies.focusSessionRepository(
                        context = context,
                        currentUserId = currentUserId
                    ),
                profilePreferencesStore =
                    AppDependencies.profilePreferencesStore(context),
                currentUserId = currentUserId
            )
        }
    }
}
