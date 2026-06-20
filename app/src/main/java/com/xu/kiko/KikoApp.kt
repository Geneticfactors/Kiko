package com.xu.kiko

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.ui.component.LoadingContent
import com.xu.kiko.ui.screen.login.LoginRoute
import com.xu.kiko.ui.screen.main.MainRoute
import com.xu.kiko.ui.screen.register.RegisterRoute
import kotlinx.coroutines.launch

@Composable
fun KikoApp(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val authRepository = remember(context) {
        AppDependencies.authRepository(context)
    }
    val coroutineScope = rememberCoroutineScope()
    val sessionState = authRepository.observeSession()
        .collectAsAuthSessionState()

    when (val state = sessionState.value) {
        AuthSessionState.Loading -> {
            AuthLoadingContent(modifier = modifier)
        }

        is AuthSessionState.SignedIn -> {
            MainRoute(
                currentUserId = state.currentUserId,
                onLoggedOut = {
                    coroutineScope.launch {
                        authRepository.logout()
                    }
                },
                modifier = modifier
            )
        }

        AuthSessionState.SignedOut -> {
            AuthRoute(modifier = modifier)
        }
    }
}

@Composable
private fun AuthRoute(
    modifier: Modifier = Modifier
) {
    var destination by rememberSaveable {
        mutableStateOf(AuthDestination.Login)
    }

    when (destination) {
        AuthDestination.Login -> {
            LoginRoute(
                onOpenRegister = {
                    destination = AuthDestination.Register
                },
                onLoggedIn = {},
                modifier = modifier
            )
        }

        AuthDestination.Register -> {
            RegisterRoute(
                onNavigateBack = {
                    destination = AuthDestination.Login
                },
                onRegistered = {},
                modifier = modifier
            )
        }
    }
}

@Composable
private fun AuthLoadingContent(
    modifier: Modifier = Modifier
) {
    LoadingContent(modifier = modifier)
}

@Composable
private fun kotlinx.coroutines.flow.Flow<String?>
    .collectAsAuthSessionState(): State<AuthSessionState> {
    return produceState<AuthSessionState>(
        initialValue = AuthSessionState.Loading,
        this
    ) {
        collect { currentUserId ->
            value = if (currentUserId == null) {
                AuthSessionState.SignedOut
            } else {
                AuthSessionState.SignedIn(currentUserId)
            }
        }
    }
}

private sealed interface AuthSessionState {
    data object Loading : AuthSessionState
    data object SignedOut : AuthSessionState
    data class SignedIn(val currentUserId: String) : AuthSessionState
}

private enum class AuthDestination {
    Login,
    Register
}
