package dev.ridill.oar.settings.domain.appLock

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.core.content.ContextCompat
import dev.ridill.oar.core.domain.util.tryOrNull

class AppLockServiceManager(
    private val context: Context
) {
    fun startAppUnlockedIndicator() {
        startServiceWithAction(AppLockService.Action.START_SERVICE)
    }

    fun stopAppUnlockedIndicator() {
        startServiceWithAction(AppLockService.Action.STOP_SERVICE)
    }

    fun startAppAutoLockTimer() {
        startServiceWithAction(AppLockService.Action.START_AUTO_LOCK_TIMER)
    }

    fun stopAppLockTimer() {
        startServiceWithAction(AppLockService.Action.STOP_AUTO_LOCK_TIMER)
    }

    private fun startServiceWithAction(
        serviceAction: AppLockService.Action
    ) {
        tryOrNull(
            AppLockServiceManager::class.java.name
        ) {
            val serviceIntent = Intent(context, AppLockService::class.java).apply {
                action = serviceAction.name
            }
            Handler(Looper.getMainLooper()).post {
                tryOrNull {
                    ContextCompat.startForegroundService(
                        context,
                        serviceIntent
                    )
                }
            }
        }
    }
}