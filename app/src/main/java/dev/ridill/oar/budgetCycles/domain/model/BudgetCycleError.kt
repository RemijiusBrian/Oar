package dev.ridill.oar.budgetCycles.domain.model

import dev.ridill.oar.core.domain.model.Error

enum class BudgetCycleError : Error {
    CREATION_FAILED,
    CYCLE_NOT_ACTIVE,
    CYCLE_NOT_FOUND,
    UNKNOWN
}