package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.folders.domain.model.AggregateType
import java.util.Currency

data class BudgetCycleSummary(
    val aggregateAmount: Double,
    val aggregateType: AggregateType,
    val currency: Currency
)