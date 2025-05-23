package dev.ridill.oar.budgetCycles.domain.model

import java.time.LocalDate
import java.util.Currency

data class BudgetCycleEntry(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Double,
    val currency: Currency,
    val status: CycleStatus
)