package com.xu.kiko.ui.screen.register

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.repository.AuthRepository

/**
 * 注册页面 ViewModel 工厂类
 * 负责创建 [RegisterViewModel] 并注入所需依赖
 */
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
        /**
         * 从 [Context] 创建 [RegisterViewModelFactory]
         * 通过 [AppDependencies] 获取所需依赖
         */
        fun fromContext(context: Context): RegisterViewModelFactory {
            return RegisterViewModelFactory(
                AppDependencies.authRepository(context)
            )
        }
    }
}
