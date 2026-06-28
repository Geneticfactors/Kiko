package com.xu.kiko.notification

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.ServiceCompat
import com.xu.kiko.data.AppDependencies
import com.xu.kiko.domain.model.FocusSession
import com.xu.kiko.domain.model.FocusSessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class FocusTimerForegroundService : Service() {
    private val serviceScope =
        CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private var tickerJob: Job? = null
    private var currentUserId: String? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {
        currentUserId = intent
            ?.getStringExtra(EXTRA_USER_ID)
            ?: currentUserId

        when (intent?.action) {
            ACTION_START -> startTicker()
            ACTION_PAUSE -> runSessionAction { session, focusedSeconds ->
                focusSessionRepository().pauseSession(
                    sessionId = session.id,
                    focusedSeconds = focusedSeconds
                )
            }
            ACTION_RESUME -> runSessionAction { session, _ ->
                focusSessionRepository().resumeSession(session.id)
            }
            ACTION_STOP -> runSessionAction(stopAfterAction = true) {
                    session,
                    focusedSeconds
                ->
                focusSessionRepository().cancelSession(
                    sessionId = session.id,
                    focusedSeconds = focusedSeconds
                )
            }
            ACTION_DISMISS -> stopForegroundService()
            else -> startTicker()
        }

        return START_STICKY
    }

    override fun onDestroy() {
        tickerJob?.cancel()
        super.onDestroy()
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = serviceScope.launch {
            while (isActive) {
                val session = focusSessionRepository().getActiveSession()
                if (session == null) {
                    stopForegroundService()
                    return@launch
                }

                val focusedSeconds = focusedSecondsFor(session)
                val remainingSeconds =
                    (session.plannedDurationSeconds - focusedSeconds)
                        .coerceAtLeast(0L)

                if (remainingSeconds <= 0L) {
                    completeSessionFromService(session)
                    stopForegroundService()
                    return@launch
                }

                showForegroundNotification(
                    remainingSeconds = remainingSeconds,
                    isRunning =
                        session.status == FocusSessionStatus.RUNNING
                )

                delay(TICK_DELAY_MILLIS)
            }
        }
    }

    private fun runSessionAction(
        stopAfterAction: Boolean = false,
        action: suspend (FocusSession, Long) -> Boolean
    ) {
        serviceScope.launch {
            val session = focusSessionRepository().getActiveSession()
                ?: run {
                    stopForegroundService()
                    return@launch
                }
            action(session, focusedSecondsFor(session))
            if (stopAfterAction) {
                stopForegroundService()
            } else {
                startTicker()
            }
        }
    }

    private suspend fun completeSessionFromService(session: FocusSession) {
        val completed = focusSessionRepository().completeSession(
            sessionId = session.id,
            focusedSeconds = session.plannedDurationSeconds
        )
        if (completed) {
            taskRepository().incrementCompletedPomodoros(session.taskId)
            AppDependencies.focusNotificationCoordinator(
                context = applicationContext,
                currentUserId = userId()
            ).onTimerCompleted()
        }
    }

    private fun showForegroundNotification(
        remainingSeconds: Long,
        isRunning: Boolean
    ) {
        val notification = AppDependencies
            .notificationController(applicationContext)
            .focusTimerNotification(
                title = "专注进行中",
                content = "剩余 ${formatRemainingTime(remainingSeconds)}",
                isRunning = isRunning
            )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceCompat.startForeground(
                this,
                KikoNotificationController.FOCUS_TIMER_NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(
                KikoNotificationController.FOCUS_TIMER_NOTIFICATION_ID,
                notification
            )
        }
    }

    private fun stopForegroundService() {
        tickerJob?.cancel()
        ServiceCompat.stopForeground(
            this,
            ServiceCompat.STOP_FOREGROUND_REMOVE
        )
        stopSelf()
    }

    private suspend fun focusSessionRepository() =
        AppDependencies.focusSessionRepository(
            context = applicationContext,
            currentUserId = userId()
        )

    private suspend fun taskRepository() =
        AppDependencies.taskRepository(
            context = applicationContext,
            currentUserId = userId()
        )

    private suspend fun userId(): String {
        currentUserId?.let { return it }
        return AppDependencies.sessionStore(applicationContext)
            .observeCurrentUserId()
            .first()
            .orEmpty()
            .also { currentUserId = it }
    }

    private fun focusedSecondsFor(session: FocusSession): Long {
        val runningSeconds =
            if (
                session.status == FocusSessionStatus.RUNNING &&
                session.lastStartedAtEpochMillis != null
            ) {
                ((System.currentTimeMillis() -
                    session.lastStartedAtEpochMillis) / 1000L)
                    .coerceAtLeast(0L)
            } else {
                0L
            }

        return (session.focusedDurationSeconds + runningSeconds)
            .coerceAtMost(session.plannedDurationSeconds)
    }

    private fun formatRemainingTime(seconds: Long): String {
        val minutes = seconds / SECONDS_PER_MINUTE
        val remainingSeconds = seconds % SECONDS_PER_MINUTE
        return "%02d:%02d".format(minutes, remainingSeconds)
    }

    companion object {
        const val ACTION_START = "com.xu.kiko.notification.START"
        const val ACTION_PAUSE = "com.xu.kiko.notification.PAUSE"
        const val ACTION_RESUME = "com.xu.kiko.notification.RESUME"
        const val ACTION_STOP = "com.xu.kiko.notification.STOP"
        const val ACTION_DISMISS = "com.xu.kiko.notification.DISMISS"
        const val EXTRA_USER_ID = "extra_user_id"
        private const val TICK_DELAY_MILLIS = 1_000L
        private const val SECONDS_PER_MINUTE = 60L
    }
}
