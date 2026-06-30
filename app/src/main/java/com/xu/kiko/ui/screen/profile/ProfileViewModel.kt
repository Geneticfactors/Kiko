package com.xu.kiko.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.data.profile.ProfilePreferencesStore
import com.xu.kiko.data.theme.ThemePreferencesStore
import com.xu.kiko.domain.model.FocusHeatmapDay
import com.xu.kiko.domain.repository.AuthRepository
import com.xu.kiko.domain.repository.FocusSessionRepository
import com.xu.kiko.ui.theme.KikoThemeColor
import java.util.Calendar
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * 个人中心页面 ViewModel
 * 负责管理用户信息、主题设置和退出登录等逻辑
 */
class ProfileViewModel(
    // 认证仓库
    private val authRepository: AuthRepository,

    // 专注会话仓库
    private val focusSessionRepository: FocusSessionRepository,

    // 个人偏好设置存储
    private val profilePreferencesStore: ProfilePreferencesStore,

    // 主题偏好设置存储
    private val themePreferencesStore: ThemePreferencesStore,

    // 当前用户 ID
    private val currentUserId: String,

    // 当前时间提供者，用于测试
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    // 内部 UI 状态流
    private val _uiState =
        MutableStateFlow(defaultProfileUiState())

    // 暴露给 UI 层的只读状态流
    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    // 内部副作用通道
    private val _effects =
        Channel<ProfileUiEffect>(Channel.BUFFERED)

    // 暴露给 UI 层的副作用流
    val effects: Flow<ProfileUiEffect> =
        _effects.receiveAsFlow()

    /**
     * ViewModel 初始化
     * 加载用户信息、热力图数据、头像和主题偏好
     */
    init {
        loadCurrentUser()
        observeHeatmapDays()
        observeAvatarImage()
        observeThemePreferences()
    }

    /**
     * 处理用户操作
     * 根据 [ProfileUiAction] 分发到对应的处理方法
     *
     * @param action 用户操作意图
     */
    fun onAction(action: ProfileUiAction) {
        when (action) {
            is ProfileUiAction.SetDarkModeEnabled ->
                setDarkModeEnabled(action.enabled)

            ProfileUiAction.OpenThemeColorDialog ->
                setThemeColorDialogVisible(visible = true)

            ProfileUiAction.CloseThemeColorDialog ->
                setThemeColorDialogVisible(visible = false)

            is ProfileUiAction.SelectThemeColor ->
                selectThemeColor(action.color)

            is ProfileUiAction.SelectAvatarImage ->
                selectAvatarImage(action.uri)

            ProfileUiAction.OpenNotificationSettings ->
                openNotificationSettings()

            ProfileUiAction.RequestLogout ->
                setLogoutConfirmDialogVisible(visible = true)

            ProfileUiAction.CancelLogout ->
                setLogoutConfirmDialogVisible(visible = false)

            ProfileUiAction.ConfirmLogout ->
                confirmLogout()
        }
    }

    /**
     * 设置深色模式开关
     * 更新 UI 状态并持久化到偏好设置
     *
     * @param enabled 是否开启深色模式
     */
    private fun setDarkModeEnabled(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(darkModeEnabled = enabled)
        }
        viewModelScope.launch {
            themePreferencesStore.setDarkModeEnabled(enabled)
        }
    }

    /**
     * 设置主题颜色选择对话框可见性
     *
     * @param visible 是否显示对话框
     */
    private fun setThemeColorDialogVisible(visible: Boolean) {
        _uiState.update { state ->
            state.copy(showThemeColorDialog = visible)
        }
    }

    /**
     * 选择主题颜色
     * 更新 UI 状态、关闭对话框并持久化到偏好设置
     *
     * @param color 选中的主题颜色
     */
    private fun selectThemeColor(color: KikoThemeColor) {
        _uiState.update { state ->
            state.copy(
                selectedThemeColor = color,
                showThemeColorDialog = false
            )
        }
        viewModelScope.launch {
            themePreferencesStore.setThemeColor(color)
        }
    }

    /**
     * 选择头像图片
     * 将图片保存到本地存储
     *
     * @param uri 头像图片的 URI
     */
    private fun selectAvatarImage(uri: String) {
        viewModelScope.launch {
            runCatching {
                profilePreferencesStore.saveAvatarImage(
                    userId = currentUserId,
                    sourceUri = uri
                )
            }
        }
    }

    /**
     * 打开通知设置页面
     * 通过副作用通知 UI 层进行导航
     */
    private fun openNotificationSettings() {
        _effects.trySend(
            ProfileUiEffect.NavigateToNotificationSettings
        )
    }

    /**
     * 设置退出登录确认对话框可见性
     * 如果正在退出登录中，忽略请求
     *
     * @param visible 是否显示对话框
     */
    private fun setLogoutConfirmDialogVisible(visible: Boolean) {
        if (_uiState.value.isLoggingOut) {
            return
        }

        _uiState.update { state ->
            state.copy(showLogoutConfirmDialog = visible)
        }
    }

    /**
     * 确认退出登录
     * 如果正在退出登录中，忽略请求
     * 通过副作用通知 UI 层执行退出登录操作
     */
    private fun confirmLogout() {
        if (_uiState.value.isLoggingOut) {
            return
        }

        viewModelScope.launch {
            _uiState.update { state ->
                state.copy(isLoggingOut = true)
            }

            _effects.send(ProfileUiEffect.LoggedOut)

            _uiState.update { state ->
                state.copy(
                    isLoggingOut = false,
                    showLogoutConfirmDialog = false
                )
            }
        }
    }

    /**
     * 加载当前用户信息
     * 获取昵称、计算注册天数和头像文本
     */
    private fun loadCurrentUser() {
        viewModelScope.launch {
            val user = authRepository.getCurrentUser()
                ?: return@launch

            _uiState.update { state ->
                state.copy(
                    nickname = user.nickname,
                    avatarText = avatarTextFor(user.nickname),
                    joinedDays = joinedDaysSince(
                        user.createdAtEpochMillis
                    )
                )
            }
        }
    }

    /**
     * 创建默认的个人中心 UI 状态
     */
    private fun defaultProfileUiState(): ProfileUiState {
        val nickname = "用户昵称"

        return ProfileUiState(
            nickname = nickname,
            avatarText = avatarTextFor(nickname),
            joinedDays = 32,
            focusedDays = emptyList(),
            selectedThemeColor = KikoThemeColor.BLUE
        )
    }

    /**
     * 根据昵称生成头像文本
     * 取昵称前两个字符，为空时返回默认值
     *
     * @param nickname 用户昵称
     * @return 头像文本（最多两个字符）
     */
    private fun avatarTextFor(nickname: String): String {
        return nickname
            .trim()
            .take(AVATAR_TEXT_LENGTH)
            .ifEmpty { DEFAULT_AVATAR_TEXT }
    }

    /**
     * 计算从注册到现在的天数
     *
     * @param createdAtEpochMillis 注册时间戳（毫秒）
     * @return 注册天数
     */
    private fun joinedDaysSince(createdAtEpochMillis: Long): Int {
        val startDay = startOfDay(createdAtEpochMillis)
        val today = startOfDay(nowProvider())
        val elapsedDays = ((today - startDay) / MILLIS_PER_DAY)
            .toInt()
            .coerceAtLeast(0)
        return elapsedDays + 1
    }

    /**
     * 获取指定时间戳对应的当天开始时间
     * 将时间戳转换为当天 00:00:00.000
     *
     * @param epochMillis 时间戳（毫秒）
     * @return 当天开始时间戳
     */
    private fun startOfDay(epochMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

    /**
     * 观察热力图数据变化
     * 监听专注会话仓库的热力图天数变化并更新 UI
     */
    private fun observeHeatmapDays() {
        viewModelScope.launch {
            focusSessionRepository.observeHeatmapDays()
                .catch {
                    _uiState.update { state ->
                        state.copy(focusedDays = emptyList())
                    }
                }
                .collect { days ->
                    _uiState.update { state ->
                        state.copy(
                            focusedDays = days.map { day ->
                                day.toProfileCalendarDayUiModel()
                            }
                        )
                    }
                }
        }
    }

    /**
     * 观察头像图片路径变化
     * 监听偏好设置中的头像路径变化并更新 UI
     */
    private fun observeAvatarImage() {
        viewModelScope.launch {
            profilePreferencesStore.observeAvatarImagePath(currentUserId)
                .catch {
                    _uiState.update { state ->
                        state.copy(avatarImagePath = null)
                    }
                }
                .collect { avatarImagePath ->
                    _uiState.update { state ->
                        state.copy(avatarImagePath = avatarImagePath)
                    }
                }
        }
    }

    /**
     * 观察主题偏好设置变化
     * 监听深色模式和主题颜色的变化并更新 UI
     */
    private fun observeThemePreferences() {
        viewModelScope.launch {
            themePreferencesStore.observeThemePreferences()
                .catch {
                    _uiState.update { state ->
                        state.copy(
                            darkModeEnabled = false,
                            selectedThemeColor = KikoThemeColor.BLUE
                        )
                    }
                }
                .collect { preferences ->
                    _uiState.update { state ->
                        state.copy(
                            darkModeEnabled = preferences.darkModeEnabled,
                            selectedThemeColor = preferences.themeColor
                        )
                    }
                }
        }
    }

    /**
     * 将 Domain [FocusHeatmapDay] 转换为 UI 模型 [ProfileCalendarDayUiModel]
     *
     * @return UI 模型
     */
    private fun FocusHeatmapDay.toProfileCalendarDayUiModel():
        ProfileCalendarDayUiModel {
        val safePomodoroCount = completedPomodoros.coerceAtLeast(0)
        return ProfileCalendarDayUiModel(
            id = dateEpochMillis.toString(),
            dateEpochMillis = dateEpochMillis,
            pomodoroCount = safePomodoroCount,
            intensity = heatmapIntensityFor(safePomodoroCount),
            isFuture = isFuture
        )
    }

    private companion object {
        // 头像文本最大长度
        const val AVATAR_TEXT_LENGTH = 2

        // 默认头像文本
        const val DEFAULT_AVATAR_TEXT = "FF"

        // 一天的毫秒数
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}

/**
 * 计算热力图强度值
 * 根据番茄钟数量返回 0-4 的强度值
 *
 * @param pomodoroCount 番茄钟数量
 * @return 热力图强度（0-4）
 */
internal fun heatmapIntensityFor(pomodoroCount: Int): Int {
    return pomodoroCount.coerceIn(
        minimumValue = 0,
        maximumValue = 4
    )
}
