package dev.ridill.oar.schedules.domain.model

import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.transactions.domain.model.Transaction
import dev.ridill.oar.transactions.domain.model.TransactionType
import java.time.LocalDateTime
import java.util.Currency

data class Schedule(
    val id: Long,
    val amount: Double,
    val note: String?,
    val currency: Currency,
    val type: TransactionType,
    val tagId: Long?,
    val folderId: Long?,
    val repetition: ScheduleRepetition,
    val nextPaymentTimestamp: LocalDateTime?,
    val lastPaymentTimestamp: LocalDateTime?
) {
    companion object {
        fun fromTransaction(
            transaction: Transaction,
            repeatMode: ScheduleRepetition,
        ): Schedule = Schedule(
            id = transaction.id,
            amount = transaction.amount.toDoubleOrNull().orZero(),
            currency = transaction.currency,
            note = transaction.note.ifEmpty { null },
            type = transaction.type,
            repetition = repeatMode,
            tagId = transaction.tagId,
            folderId = transaction.folderId,
            nextPaymentTimestamp = transaction.timestamp,
            lastPaymentTimestamp = null
        )
    }
}