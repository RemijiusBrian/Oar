package dev.ridill.oar.budgetCycles.data

import dev.ridill.oar.budgetCycles.data.local.entity.BudgetCycleEntity
import dev.ridill.oar.budgetCycles.data.local.view.BudgetCycleDetailsView
import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.CycleHistoryEntry
import dev.ridill.oar.core.domain.util.LocaleUtil

fun BudgetCycleDetailsView.toEntry(): BudgetCycleEntry = BudgetCycleEntry(
    id = id,
    startDate = startDate,
    endDate = endDate,
    budget = budget,
    currency = LocaleUtil.currencyForCode(currencyCode),
    active = active
)

fun BudgetCycleEntry.toEntity(): BudgetCycleEntity = BudgetCycleEntity(
    id = id,
    startDate = startDate,
    endDate = endDate,
    budget = budget,
    currencyCode = currency.currencyCode,
)

fun BudgetCycleDetailsView.toHistoryEntry(): CycleHistoryEntry = CycleHistoryEntry(
    id = id,
    startDate = startDate,
    endDate = endDate,
    budget = budget,
    currency = LocaleUtil.currencyForCode(currencyCode),
    active = active,
    aggregate = aggregate
)