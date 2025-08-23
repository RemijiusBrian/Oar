package dev.ridill.oar.budgetCycles.presentation.cycleHistory

import dev.ridill.oar.budgetCycles.domain.model.CycleHistoryEntry

data class BudgetCyclesState(
    val activeCycle: CycleHistoryEntry? = null,
    val showCycleCompleteAction: Boolean = false,
    val showCycleCompletionWarning: Boolean = false
)