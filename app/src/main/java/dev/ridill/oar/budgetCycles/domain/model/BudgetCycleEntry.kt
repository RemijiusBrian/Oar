package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.core.domain.util.DateUtil
import java.time.LocalDate
import java.util.Currency

data class BudgetCycleEntry(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
    val budget: Long,
    val currency: Currency,
    val active: Boolean
) {
    val description: String
        get() = DateUtil.prettyDateRange(startDate, endDate)
}