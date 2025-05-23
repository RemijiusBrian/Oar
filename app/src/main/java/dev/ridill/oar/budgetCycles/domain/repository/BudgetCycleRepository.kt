package dev.ridill.oar.budgetCycles.domain.repository

import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleConfig
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleError
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleSummary
import dev.ridill.oar.core.domain.model.Result
import java.time.LocalDate
import java.util.Currency

interface BudgetCycleRepository {
    suspend fun getCycleConfig(): BudgetCycleConfig
    suspend fun updateScheduleCycleConfig(config: BudgetCycleConfig)
    suspend fun getLastCycle(): BudgetCycleEntry?
    fun scheduleCycleCompletion(cycle: BudgetCycleEntry)
    suspend fun scheduleLastCycleOrNew(): Result<Unit, BudgetCycleError>
    suspend fun createNewCycleAndScheduleCompletion(
        startDate: LocalDate,
        endDate: LocalDate,
        budget: Double,
        currency: Currency
    ): Result<Unit, BudgetCycleError>

    suspend fun completeCurrentCycleAndStartNext(id: Long): Result<BudgetCycleSummary, BudgetCycleError>
}