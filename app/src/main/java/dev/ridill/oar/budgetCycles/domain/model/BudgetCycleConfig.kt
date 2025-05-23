package dev.ridill.oar.budgetCycles.domain.model

import java.util.Currency

data class BudgetCycleConfig(
    val budget: Double,
    val currency: Currency,
    val startDay: CycleStartDay
)