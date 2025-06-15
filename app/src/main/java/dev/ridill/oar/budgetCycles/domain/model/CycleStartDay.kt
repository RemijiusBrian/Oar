package dev.ridill.oar.budgetCycles.domain.model

sealed class CycleStartDay(
    val type: CycleStartDayType
) {
    data object FirstDayOfMonth : CycleStartDay(CycleStartDayType.FIRST_DAY_OF_MONTH)
    data object LastDayOfMonth : CycleStartDay(CycleStartDayType.LAST_DAY_OF_MONTH)
    data class SpecificDayOfMonth(val dayOfMonth: Int) :
        CycleStartDay(CycleStartDayType.SPECIFIC_DAY_OF_MONTH)
}

enum class CycleStartDayType {
    FIRST_DAY_OF_MONTH,
    LAST_DAY_OF_MONTH,
    SPECIFIC_DAY_OF_MONTH
}