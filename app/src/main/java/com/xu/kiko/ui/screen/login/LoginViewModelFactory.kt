package com.xu.kiko.ui.screen.login

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.AuthRepository

/**
 * 登录页面 ViewModel 工厂类
 * 负责创建 [LoginViewModel] 并注入所需依赖
 */
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
        /**
         * 从 [Context] 创建 [LoginViewModelFactory]
         * 通过 [AppDependencies] 获取所需依赖
         */
        fun fromContext(context: Context): LoginViewModelFactory {
            return LoginViewModelFactory(
                AppDependencies.authRepository(context)
            )
        }
    }
}
