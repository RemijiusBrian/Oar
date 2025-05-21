package dev.ridill.oar.transactions.domain.autoDetection

import dev.ridill.oar.transactions.domain.model.TransactionType
import java.time.LocalDateTime

interface TransactionDataExtractor {
    fun isSupportedLanguage(message: String): Boolean
    fun isOriginValidOrg(originatingAddress: String): Boolean

    @Throws(
        AmountExtractionFailedThrowable::class,
        TransactionTypeExtractionFailedThrowable::class,
        TransactionNoteBuildFailedThrowable::class,
        TimestampExtractionFailedThrowable::class,
        TransactionDataExtractionFailedThrowable::class
    )
    fun extractData(messageBody: String): ExtractedTransactionData
}

data class ExtractedTransactionData(
    val amount: Double,
    val paymentTimestamp: LocalDateTime,
    val transactionType: TransactionType,
    val note: String?
)

open class TransactionDataExtractionFailedThrowable(message: String) : Throwable(message)