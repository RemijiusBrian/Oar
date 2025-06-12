package dev.ridill.oar.settings.domain.appInit

import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dev.ridill.oar.R
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.logI
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.di.AppInitFeature
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class CurrencyInitWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted params: WorkerParameters,
    @AppInitFeature private val notificationHelper: NotificationHelper<Unit>,
    private val currencyRepo: CurrencyRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        startForeground()
        try {

            logI(CurrencyInitWorker::class.simpleName) { "Running currencyList init" }
            currencyRepo.initCurrenciesList()
            logI(CurrencyInitWorker::class.simpleName) { "Initialization complete" }
            Result.success()
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t, CurrencyInitWorker::class.simpleName) { "AppInit failed" }
            Result.failure()
        }
    }

    private suspend fun startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            setForeground(
                ForegroundInfo(
                    AppInitWorkManager.APP_INIT_NOTIFICATION_ID.hashCode(),
                    notificationHelper.buildBaseNotification()
                        .setContentTitle(
                            appContext.getString(
                                R.string.initializing_app,
                                appContext.getString(R.string.app_name)
                            )
                        )
                        .setProgress(100, 0, true)
                        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                        .build(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            )
        } else {
            setForeground(
                ForegroundInfo(
                    AppInitWorkManager.APP_INIT_NOTIFICATION_ID.hashCode(),
                    notificationHelper.buildBaseNotification()
                        .setContentTitle(
                            appContext.getString(
                                R.string.initializing_app,
                                appContext.getString(R.string.app_name)
                            )
                        )
                        .setProgress(100, 0, true)
                        .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
                        .build()
                )
            )
        }
    }
}