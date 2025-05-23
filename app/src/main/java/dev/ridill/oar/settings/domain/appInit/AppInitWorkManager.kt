package dev.ridill.oar.settings.domain.appInit

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dev.ridill.oar.core.domain.util.toUUID

class AppInitWorkManager(
    private val context: Context
) {
    companion object {
        const val APP_INIT_NOTIFICATION_ID = "APP_INIT_NOTIFICATION"
    }

    private val workManager = WorkManager.getInstance(context)

    private val appInitWorkerName: String
        get() = "${context.packageName}.APP_INIT_WORKER"

    fun startAppInitWorker() {
        val workRequest = OneTimeWorkRequestBuilder<AppInitWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setId(appInitWorkerName.toUUID())
            .build()

        workManager.enqueueUniqueWork(
            appInitWorkerName,
            ExistingWorkPolicy.KEEP,
            workRequest
        )
    }
}