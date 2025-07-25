package dev.ridill.oar.budgetCycles.domain.repository

import androidx.paging.PagingData
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleConfig
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleError
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleSummary
import dev.ridill.oar.budgetCycles.domain.model.CycleDurationUnit
import dev.ridill.oar.budgetCycles.domain.model.CycleSelector
import dev.ridill.oar.budgetCycles.domain.model.CycleStartDay
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.Empty
import kotlinx.coroutines.flow.Flow
import java.time.YearMonth
import java.util.Currency

interface BudgetCycleRepository {
    fun getActiveCycleFlow(): Flow<BudgetCycleEntry?>
    suspend fun getActiveCycle(): BudgetCycleEntry?
    suspend fun getCycleConfig(): BudgetCycleConfig

    suspend fun updateCycleConfig(
        budget: Long,
        currency: Currency,
        startDay: CycleStartDay,
        duration: Long,
        durationUnit: CycleDurationUnit
    )

    suspend fun updateBudgetForActiveCycle(value: Long)
    suspend fun updateCurrencyForActiveCycle(currency: Currency)
    suspend fun getLastCycle(): BudgetCycleEntry?
    fun scheduleCycleCompletion(cycle: BudgetCycleEntry): Result<Unit, BudgetCycleError>
    suspend fun scheduleLastCycleOrNew(): Result<Unit, BudgetCycleError>

    suspend fun createNewCycleAndScheduleCompletion(
        month: YearMonth,
        startNow: Boolean = false
    ): Result<Unit, BudgetCycleError>

    suspend fun createCycleEntryFromConfigForMonth(
        month: YearMonth,
        startNow: Boolean = false
    ): BudgetCycleEntry

    suspend fun updateConfigAndCreateNewCycle(
        budget: Long,
        currency: Currency,
        startDay: CycleStartDay,
        month: YearMonth,
        duration: Long,
        durationUnit: CycleDurationUnit,
        startNow: Boolean = false
    ): Result<Unit, BudgetCycleError>

    suspend fun completeCycleNowAndStartNext(id: Long): Result<BudgetCycleSummary, BudgetCycleError>
    fun getCycleByIdFlow(id: Long): Flow<BudgetCycleEntry?>

    suspend fun getCyclesPagingData(
        query: String = String.Empty
    ): Flow<PagingData<CycleSelector>>
}