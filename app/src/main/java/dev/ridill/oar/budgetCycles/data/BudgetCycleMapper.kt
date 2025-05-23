package dev.ridill.oar.budgetCycles.data

import dev.ridill.oar.budgetCycles.data.local.entity.BudgetCycleEntity
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.core.domain.util.LocaleUtil

fun BudgetCycleEntity.toEntry(): BudgetCycleEntry = BudgetCycleEntry(
    id = id,
    startDate = startDate,
    endDate = endDate,
    budget = budget,
    currency = LocaleUtil.currencyForCode(currencyCode),
    status = status
)

fun BudgetCycleEntry.toEntity(): BudgetCycleEntity = BudgetCycleEntity(
    id = id,
    startDate = startDate,
    endDate = endDate,
    budget = budget,
    currencyCode = currency.currencyCode,
    status = status
)