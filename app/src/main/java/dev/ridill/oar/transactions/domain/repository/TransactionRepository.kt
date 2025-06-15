package dev.ridill.oar.transactions.domain.repository

import androidx.paging.PagingData
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.model.BasicError
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.Empty
import dev.ridill.oar.transactions.domain.model.Transaction
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionListItemUIModel
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import java.util.Currency

interface TransactionRepository {
    fun getAllTransactionsPaged(
        query: String? = String.Empty,
        cycleIds: Set<Long>? = null,
        type: TransactionType? = null,
        showExcluded: Boolean = true,
        tagIds: Set<Long>? = null,
        folderId: Long? = null,
        currency: Currency? = null
    ): Flow<PagingData<TransactionEntry>>

    fun getDateSeparatedTransactions(
        query: String? = String.Empty,
        cycleIds: Set<Long>? = null,
        type: TransactionType? = null,
        showExcluded: Boolean = true,
        tagIds: Set<Long>? = null,
        folderId: Long? = null,
        currency: Currency? = null
    ): Flow<PagingData<TransactionListItemUIModel>>

    suspend fun saveTransaction(
        cycleId: Long,
        amount: Double,
        id: Long = OarDatabase.DEFAULT_ID_LONG,
        note: String? = null,
        timestamp: LocalDateTime = DateUtil.now(),
        type: TransactionType = TransactionType.DEBIT,
        tagId: Long? = null,
        folderId: Long? = null,
        scheduleId: Long? = null,
        excluded: Boolean = false,
        currency: Currency? = null
    ): Transaction

    suspend fun deleteSafely(id: Long): Result<Unit, BasicError>
    suspend fun deleteSafely(ids: Set<Long>): Result<Unit, BasicError>
    suspend fun toggleExcluded(id: Long, excluded: Boolean)
}