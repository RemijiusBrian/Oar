package dev.ridill.oar.settings.domain.appInit

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkInfo
import androidx.work.WorkManager
import dev.ridill.oar.core.domain.util.toUUID
import kotlinx.coroutines.flow.Flow

class AppInitWorkManager(
    private val context: Context
) {
    companion object {
        const val APP_INIT_NOTIFICATION_ID = "APP_INIT_NOTIFICATION"
    }

    private val workManager = WorkManager.getInstance(context)

    private val budgetCycleInitWorkerName: String
        get() = "${context.packageName}.BUDGET_CYCLE_INIT_WORKER"

    private val currencyInitWorkerName: String
        get() = "${context.packageName}.CURRENCY_INIT_WORKER"

    fun startAppInitWorker() {
        val budgetCycleInitWorker = OneTimeWorkRequestBuilder<BudgetCycleInitWorker>()
            .setId(budgetCycleInitWorkerName.toUUID())
            .setExpedited(OutOfQuotaPolicy.DROP_WORK_REQUEST)
            .build()

        val currencyInitWorker = OneTimeWorkRequestBuilder<CurrencyInitWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setId(currencyInitWorkerName.toUUID())
            .build()

        workManager.beginUniqueWork(
            budgetCycleInitWorkerName,
            ExistingWorkPolicy.REPLACE,
            budgetCycleInitWorker
        )
            .then(currencyInitWorker)
            .enqueue()
    }

    fun getBudgetCycleInitWorkInfo(): Flow<WorkInfo?> =
        workManager.getWorkInfoByIdFlow(budgetCycleInitWorkerName.toUUID())
}