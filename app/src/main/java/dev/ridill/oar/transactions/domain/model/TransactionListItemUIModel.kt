package dev.ridill.oar.transactions.domain.model

import dev.ridill.oar.budgetCycles.domain.model.BudgetCycleEntry
import dev.ridill.oar.budgetCycles.domain.model.CycleIndicator
import java.time.LocalDateTime
import java.util.Currency

sealed class TransactionListItemUIModel {
    data class TransactionItem(
        val id: Long,
        val note: String,
        val amount: Double,
        val currency: Currency,
        val timestamp: LocalDateTime,
        val type: TransactionType,
        val excluded: Boolean,
        val cycleEntry: CycleIndicator,
        val tag: TagIndicator?,
        val folder: FolderIndicator?,
        val scheduleId: Long?
    ) : TransactionListItemUIModel() {
        constructor(transactionEntry: TransactionEntry) : this(
            transactionEntry.id,
            transactionEntry.note,
            transactionEntry.amount,
            transactionEntry.currency,
            transactionEntry.timestamp,
            transactionEntry.type,
            transactionEntry.excluded,
            transactionEntry.cycle,
            transactionEntry.tag,
            transactionEntry.folder,
            transactionEntry.scheduleId
        )
    }

    data class CycleSeparator(val cycle: CycleIndicator) : TransactionListItemUIModel()
}