package dev.ridill.oar.budgetCycles.domain.model

import java.util.Currency

data class BudgetCycleConfig(
    val budget: Long,
    val currency: Currency,
    val startDay: CycleStartDay,
    val duration: Long,
    val durationUnit: CycleDurationUnit
)