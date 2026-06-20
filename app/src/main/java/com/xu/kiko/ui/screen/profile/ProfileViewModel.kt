package com.xu.kiko.ui.screen.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xu.kiko.data.profile.ProfilePreferencesStore
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

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val focusSessionRepository: FocusSessionRepository,
    private val profilePreferencesStore: ProfilePreferencesStore,
    private val currentUserId: String,
    private val nowProvider: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(defaultProfileUiState())
    val uiState: StateFlow<ProfileUiState> =
        _uiState.asStateFlow()

    private val _effects =
        Channel<ProfileUiEffect>(Channel.BUFFERED)
    val effects: Flow<ProfileUiEffect> =
        _effects.receiveAsFlow()

    init {
        loadCurrentUser()
        observeHeatmapDays()
        observeAvatarImage()
    }

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

    private fun setDarkModeEnabled(enabled: Boolean) {
        _uiState.update { state ->
            state.copy(darkModeEnabled = enabled)
        }
    }

    private fun setThemeColorDialogVisible(visible: Boolean) {
        _uiState.update { state ->
            state.copy(showThemeColorDialog = visible)
        }
    }

    private fun selectThemeColor(color: KikoThemeColor) {
        _uiState.update { state ->
            state.copy(
                selectedThemeColor = color,
                showThemeColorDialog = false
            )
        }
    }

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

    private fun openNotificationSettings() {
        _effects.trySend(
            ProfileUiEffect.NavigateToNotificationSettings
        )
    }

    private fun setLogoutConfirmDialogVisible(visible: Boolean) {
        if (_uiState.value.isLoggingOut) {
            return
        }

        _uiState.update { state ->
            state.copy(showLogoutConfirmDialog = visible)
        }
    }

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

    private fun avatarTextFor(nickname: String): String {
        return nickname
            .trim()
            .take(AVATAR_TEXT_LENGTH)
            .ifEmpty { DEFAULT_AVATAR_TEXT }
    }

    private fun joinedDaysSince(createdAtEpochMillis: Long): Int {
        val startDay = startOfDay(createdAtEpochMillis)
        val today = startOfDay(nowProvider())
        val elapsedDays = ((today - startDay) / MILLIS_PER_DAY)
            .toInt()
            .coerceAtLeast(0)
        return elapsedDays + 1
    }

    private fun startOfDay(epochMillis: Long): Long {
        return Calendar.getInstance().apply {
            timeInMillis = epochMillis
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
    }

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
        const val AVATAR_TEXT_LENGTH = 2
        const val DEFAULT_AVATAR_TEXT = "FF"
        const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L
    }
}

internal fun heatmapIntensityFor(pomodoroCount: Int): Int {
    return pomodoroCount.coerceIn(
        minimumValue = 0,
        maximumValue = 4
    )
}
