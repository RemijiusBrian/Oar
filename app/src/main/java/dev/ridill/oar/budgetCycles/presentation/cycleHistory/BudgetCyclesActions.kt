package dev.ridill.oar.budgetCycles.presentation.cycleHistory

interface BudgetCyclesActions {
    fun onCycleOptionsClick()
    fun onCycleOptionsDismiss()
    fun onCompleteActiveCycleAction()
    fun onCompleteActiveCycleDismiss()
    fun onCompleteActiveCycleConfirm()
}