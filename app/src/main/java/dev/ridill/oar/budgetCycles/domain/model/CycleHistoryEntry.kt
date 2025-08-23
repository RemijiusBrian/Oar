package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.ui.util.TextFormat
import java.time.LocalDate
import java.util.Currency
import kotlin.math.absoluteValue

data class CycleHistoryEntry(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Long,
    val currency: Currency,
    val active: Boolean,
    val aggregate: Double
) {
    val description: String
        get() = DateUtil.prettyDateRange(startDate, endDate)

    val budgetFormatted: String
        get() = TextFormat.currency(amount = budget, currency = currency)

    val aggregateFormatted: String
        get() = TextFormat.currency(amount = aggregate.absoluteValue, currency = currency)

    val isWithinBudget: Boolean
        get() = aggregate <= budget.toDouble()
}