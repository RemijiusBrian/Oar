package dev.ridill.oar.schedules.domain.scheduleReminder

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.di.BackupFeature
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.settings.domain.appInit.AppInitWorkManager
import dev.ridill.oar.settings.domain.backup.BackupWorkManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class RestoreScheduleRemindersWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    private val repo: SchedulesRepository,
    @BackupFeature private val notificationHelper: NotificationHelper<String>,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        startForegroundService()
        try {
            repo.setAllFutureScheduleReminders()
            Result.success()
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t, RestoreScheduleRemindersWorker::class.simpleName) { "Throwable" }
            Result.failure(
                workDataOf(
                    BackupWorkManager.KEY_MESSAGE to appContext.getString(R.string.error_schedule_reminders_restore_failed)
                )
            )
        }
    }

    private suspend fun startForegroundService() {
        val notification = notificationHelper.buildBaseNotification()
            .setContentTitle(appContext.getString(R.string.restoring_schedule_reminders))
            .setProgress(100, 0, true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            setForeground(
                ForegroundInfo(
                    AppInitWorkManager.RESTORE_WORKER_NOTIFICATION_ID.hashCode(),
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            )
        } else {
            setForeground(
                ForegroundInfo(
                    AppInitWorkManager.APP_INIT_NOTIFICATION_ID.hashCode(),
                    notification
                )
            )
        }
    }
}