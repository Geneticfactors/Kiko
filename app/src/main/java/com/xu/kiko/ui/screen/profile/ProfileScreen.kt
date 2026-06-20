package com.xu.kiko.ui.screen.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.R
import com.xu.kiko.ui.component.KikoCard
import com.xu.kiko.ui.component.SecondaryButton
import com.xu.kiko.ui.theme.KikoTheme
import com.xu.kiko.ui.theme.KikoThemeColor
import com.xu.kiko.ui.theme.spacing
import java.util.Calendar

@Composable
fun ProfileScreen(
    state: ProfileUiState,
    onAction: (ProfileUiAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val avatarPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            onAction(ProfileUiAction.SelectAvatarImage(it.toString()))
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = MaterialTheme.spacing.extraLarge)
            .padding(bottom = MaterialTheme.spacing.section),
        verticalArrangement = Arrangement.spacedBy(
            MaterialTheme.spacing.large
        )
    ) {
        Text(
            text = stringResource(R.string.profile_title),
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    top = MaterialTheme.spacing.large,
                    bottom = MaterialTheme.spacing.small
                ),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        ProfileHeader(
            avatarText = state.avatarText,
            avatarImagePath = state.avatarImagePath,
            nickname = state.nickname,
            joinedDays = state.joinedDays,
            onAvatarClick = {
                avatarPickerLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        )

        FocusHeatmap(
            days = state.focusedDays,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = stringResource(R.string.profile_settings),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        KikoCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.small
            )
        ) {
            SettingsSwitchItem(
                title = stringResource(R.string.profile_dark_mode),
                checked = state.darkModeEnabled,
                onCheckedChange = { enabled ->
                    onAction(
                        ProfileUiAction.SetDarkModeEnabled(enabled)
                    )
                }
            )

            SettingsThemeColorItem(
                title = stringResource(R.string.profile_theme_color),
                color = state.selectedThemeColor.color,
                onClick = {
                    onAction(ProfileUiAction.OpenThemeColorDialog)
                }
            )

            SettingsNavigationItem(
                title = stringResource(
                    R.string.profile_notification_settings
                ),
                onClick = {
                    onAction(ProfileUiAction.OpenNotificationSettings)
                }
            )
        }

        KikoCard(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.large,
                vertical = MaterialTheme.spacing.small
            )
        ) {
            SecondaryButton(
                text = stringResource(R.string.profile_logout),
                onClick = {
                    onAction(ProfileUiAction.RequestLogout)
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

    if (state.showThemeColorDialog) {
        ThemeColorDialog(
            selectedThemeColor = state.selectedThemeColor,
            onThemeColorSelected = { color ->
                onAction(ProfileUiAction.SelectThemeColor(color))
            },
            onDismissRequest = {
                onAction(ProfileUiAction.CloseThemeColorDialog)
            }
        )
    }

    if (state.showLogoutConfirmDialog) {
        LogoutConfirmDialog(
            loggingOut = state.isLoggingOut,
            onConfirm = {
                onAction(ProfileUiAction.ConfirmLogout)
            },
            onDismissRequest = {
                onAction(ProfileUiAction.CancelLogout)
            }
        )
    }
}

private fun previewProfileState(
    selectedThemeColor: KikoThemeColor = KikoThemeColor.BLUE,
    showThemeColorDialog: Boolean = false,
    showLogoutConfirmDialog: Boolean = false
): ProfileUiState {
    return ProfileUiState(
        focusedDays = previewFocusedDays(),
        selectedThemeColor = selectedThemeColor,
        showThemeColorDialog = showThemeColorDialog,
        showLogoutConfirmDialog = showLogoutConfirmDialog
    )
}

private fun previewFocusedDays(): List<ProfileCalendarDayUiModel> {
    val focusedIndexes = setOf(
        1, 2, 4,
        7, 9, 11, 12,
        15, 16, 17, 20,
        21, 24, 26
    )
    val start = Calendar.getInstance().apply {
        firstDayOfWeek = Calendar.MONDAY
        set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.DAY_OF_YEAR, -21)
    }.timeInMillis

    return List(28) { index ->
        val pomodoroCount = if (index in focusedIndexes) {
            (index % 5).coerceAtLeast(1)
        } else {
            0
        }
        ProfileCalendarDayUiModel(
            id = index.toString(),
            dateEpochMillis = start + index * MILLIS_PER_DAY,
            pomodoroCount = pomodoroCount,
            intensity = pomodoroCount.coerceIn(0, 4),
            isFuture = index > 25
        )
    }
}

private const val MILLIS_PER_DAY = 24L * 60L * 60L * 1000L

@Preview(
    name = "Profile - Default",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ProfileScreenDefaultPreview() {
    KikoTheme {
        ProfileScreen(
            state = previewProfileState(),
            onAction = {}
        )
    }
}

@Preview(
    name = "Profile - Theme Dialog",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ProfileScreenThemeDialogPreview() {
    KikoTheme {
        ProfileScreen(
            state = previewProfileState(
                showThemeColorDialog = true
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Profile - Logout Dialog",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ProfileScreenLogoutDialogPreview() {
    KikoTheme {
        ProfileScreen(
            state = previewProfileState(
                showLogoutConfirmDialog = true
            ),
            onAction = {}
        )
    }
}

@Preview(
    name = "Profile - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun ProfileScreenDarkPreview() {
    KikoTheme(darkTheme = true) {
        ProfileScreen(
            state = previewProfileState(
                selectedThemeColor = KikoThemeColor.VIOLET
            ).copy(darkModeEnabled = true),
            onAction = {}
        )
    }
}

@Preview(
    name = "Profile - 320dp",
    showBackground = true,
    widthDp = 320,
    heightDp = 844
)
@Composable
private fun ProfileScreenNarrowPreview() {
    KikoTheme {
        ProfileScreen(
            state = previewProfileState(),
            onAction = {}
        )
    }
}

