package com.xu.kiko.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.AuthRepository

class LoginViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(authRepository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        fun fromContext(context: Context): LoginViewModelFactory {
            return LoginViewModelFactory(
                AppDependencies.authRepository(context)
            )
        }
    }
}
