package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.core.domain.util.DateUtil
import java.time.LocalDate

data class CycleSelector(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    val description: String
        get() = DateUtil.prettyDateRange(startDate, endDate)
}