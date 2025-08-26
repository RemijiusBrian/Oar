package dev.ridill.oar.budgetCycles.presentation.cycleHistory

import dev.ridill.oar.budgetCycles.domain.model.CycleHistoryEntry
import dev.ridill.oar.core.domain.util.Zero

data class BudgetCyclesState(
    val activeCycle: CycleHistoryEntry? = null,
    val showCycleCompleteAction: Boolean = false,
    val showCycleCompletionWarning: Boolean = false,
    val showCycleOptions: Boolean = false,
    val activeCycleProgressFraction: Float = Float.Zero
)