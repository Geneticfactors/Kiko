package com.xu.kiko.ui.screen.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.AuthRepository

class RegisterViewModelFactory(
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RegisterViewModel(authRepository) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        fun fromContext(context: Context): RegisterViewModelFactory {
            return RegisterViewModelFactory(
                AppDependencies.authRepository(context)
            )
        }
    }
}
