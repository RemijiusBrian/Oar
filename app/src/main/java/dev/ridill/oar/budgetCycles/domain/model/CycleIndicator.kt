package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.core.domain.util.DateUtil
import java.time.LocalDate

data class CycleIndicator(
    val id: Long,
    val startDate: LocalDate,
    val endDate: LocalDate,
) {
    val description: String
        get() = buildString {
            append(startDate.format(DateUtil.Formatters.localizedDateMedium))
            append(" - ")
            append(endDate.format(DateUtil.Formatters.localizedDateMedium))

        }
}