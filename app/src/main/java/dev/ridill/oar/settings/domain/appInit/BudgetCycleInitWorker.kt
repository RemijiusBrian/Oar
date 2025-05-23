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
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.CycleStatus
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.domain.notification.NotificationHelper
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.domain.util.logD
import dev.ridill.oar.core.domain.util.logI
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "BudgetCycleInitWorker"

@HiltWorker
class BudgetCycleInitWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val repo: BudgetCycleRepository,
    private val notificationHelper: NotificationHelper<BudgetCycleEntry>
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        startForeground()
        logI(TAG) { "$TAG started" }
        val lastCycle = repo.getLastCycle()
        logD(TAG) { "lastCycle = $lastCycle" }
        val isLastCycleActiveRightNow = lastCycle?.status == CycleStatus.ACTIVE
                && lastCycle.endDate > DateUtil.dateNow()
        logD(TAG) { "isLastCycleActiveRightNow = $isLastCycleActiveRightNow" }

        return@withContext if (isLastCycleActiveRightNow) {
            // Continue Ongoing cycle
            // Schedule it's completion alarm
            logI(TAG) { "Continuing ongoing cycle" }
            repo.scheduleCycleCompletion(lastCycle)
            Result.success()
        } else {
            logI(TAG) { "Creating new cycle" }
            // Create a new cycle
            val newCycleStartDate = DateUtil.dateNow()
                .withDayOfMonth(1) // FIXME: Change to start date from config
            val newCycleEndDate = newCycleStartDate
//                .plusMonths(1)
//                .minusDays(1) // FIXME: Change to end date from config
            repo.createNewCycleAndScheduleCompletion(
                startDate = newCycleStartDate,
                endDate = newCycleEndDate,
                budget = Double.Zero, // FIXME: Change to budget from config
                currency = LocaleUtil.defaultCurrency
            )

            Result.success()
        }
    }

    private suspend fun startForeground() {
        val notification = notificationHelper.buildBaseNotification()
            .setContentTitle(appContext.getString(R.string.starting_cycle))
            .setProgress(100, 0, true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            setForeground(
                ForegroundInfo(
                    AppInitWorkManager.APP_INIT_NOTIFICATION_ID.hashCode(),
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