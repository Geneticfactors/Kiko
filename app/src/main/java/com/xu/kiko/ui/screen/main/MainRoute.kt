package com.xu.kiko.ui.screen.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.xu.kiko.ui.component.KikoBottomNavDestination
import com.xu.kiko.ui.component.KikoBottomNavigationBar
import com.xu.kiko.ui.screen.focus.FocusRoute
import com.xu.kiko.ui.screen.notification.NotificationSettingsRoute
import com.xu.kiko.ui.screen.profile.ProfileRoute
import com.xu.kiko.ui.screen.statistics.StatisticsRoute
import com.xu.kiko.ui.screen.tasks.TasksRoute
import com.xu.kiko.ui.theme.KikoTheme

@Composable
fun MainRoute(
    currentUserId: String,
    onLoggedOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDestination by rememberSaveable {
        mutableStateOf(KikoBottomNavDestination.HOME)
    }
    var showNotificationSettings by rememberSaveable {
        mutableStateOf(false)
    }

    if (showNotificationSettings) {
        NotificationSettingsRoute(
            onNavigateBack = {
                showNotificationSettings = false
            },
            modifier = modifier.fillMaxSize()
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            KikoBottomNavigationBar(
                selectedDestination = selectedDestination,
                onDestinationSelected = { destination ->
                    selectedDestination = destination
                }
            )
        }
    ) { innerPadding ->
        when (selectedDestination) {
            KikoBottomNavDestination.HOME -> {
                FocusRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onNavigateToTasks = {
                        selectedDestination = KikoBottomNavDestination.TASKS
                    }
                )
            }

            KikoBottomNavDestination.TASKS -> {
                TasksRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            KikoBottomNavDestination.STATISTICS -> {
                StatisticsRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            KikoBottomNavDestination.PROFILE -> {
                ProfileRoute(
                    currentUserId = currentUserId,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onNavigateToNotificationSettings = {
                        showNotificationSettings = true
                    },
                    onLoggedOut = onLoggedOut
                )
            }
        }
    }
}

@Composable
private fun MainPlaceholderContent(
    destination: KikoBottomNavDestination,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(destination.labelRes),
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleLarge
        )
    }
}

@Preview(
    name = "Main - Light",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun MainRouteLightPreview() {
    KikoTheme {
        MainRoute(
            currentUserId = "preview_user",
            onLoggedOut = {}
        )
    }
}

@Preview(
    name = "Main - Dark",
    showBackground = true,
    widthDp = 390,
    heightDp = 844
)
@Composable
private fun MainRouteDarkPreview() {
    KikoTheme(darkTheme = true) {
        MainRoute(
            currentUserId = "preview_user",
            onLoggedOut = {}
        )
    }
}
