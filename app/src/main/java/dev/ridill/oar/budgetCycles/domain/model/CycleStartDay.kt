package dev.ridill.oar.budgetCycles.domain.model

import java.time.DayOfWeek

sealed class CycleStartDay(
    val type: CycleStartDayType
) {
    data object LastDayOfMonth : CycleStartDay(CycleStartDayType.LAST_DAY_OF_MONTH)

    data class LastDayOfWeekOfMonth(val weekDays: Set<DayOfWeek>) :
        CycleStartDay(CycleStartDayType.LAST_DAY_OF_WEEK_OF_MONTH)

    data class SpecificDayOfMonth(val dayOfMonth: Int) :
        CycleStartDay(CycleStartDayType.SPECIFIC_DAY_OF_MONTH)
}

enum class CycleStartDayType {
    LAST_DAY_OF_MONTH,
    LAST_DAY_OF_WEEK_OF_MONTH,
    SPECIFIC_DAY_OF_MONTH
}