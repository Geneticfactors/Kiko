package com.xu.kiko.ui.screen.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.data.profile.ProfilePreferencesStore
import com.xu.kiko.data.theme.ThemePreferencesStore
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.FocusSessionRepository

/**
 * 个人中心页面 ViewModel 工厂类
 * 负责创建 [ProfileViewModel] 并注入所需依赖
 */
class ProfileViewModelFactory(
    // 认证仓库
    private val authRepository: AuthRepository,

    // 专注会话仓库
    private val focusSessionRepository: FocusSessionRepository,

    // 个人偏好设置存储
    private val profilePreferencesStore: ProfilePreferencesStore,

    // 主题偏好设置存储
    private val themePreferencesStore: ThemePreferencesStore,

    // 当前用户 ID
    private val currentUserId: String
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProfileViewModel(
                authRepository = authRepository,
                focusSessionRepository = focusSessionRepository,
                profilePreferencesStore = profilePreferencesStore,
                themePreferencesStore = themePreferencesStore,
                currentUserId = currentUserId
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class: ${modelClass.name}"
        )
    }

    companion object {
        /**
         * 从 [Context] 创建 [ProfileViewModelFactory]
         * 通过 [AppDependencies] 获取所需依赖
         */
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
                themePreferencesStore =
                    AppDependencies.themePreferencesStore(context),
                currentUserId = currentUserId
            )
        }
    }
}
