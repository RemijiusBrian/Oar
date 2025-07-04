package dev.ridill.oar.transactions.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.insertSeparators
import androidx.paging.map
import androidx.room.withTransaction
import dev.ridill.oar.R
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.model.BasicError
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.logE
import dev.ridill.oar.core.domain.util.rethrowIfCoroutineCancellation
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.local.entity.TransactionEntity
import dev.ridill.oar.transactions.data.local.views.TransactionDetailsView
import dev.ridill.oar.transactions.data.toTransaction
import dev.ridill.oar.transactions.data.toTransactionListItem
import dev.ridill.oar.transactions.domain.model.Transaction
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionListItemUIModel
import dev.ridill.oar.transactions.domain.model.TransactionType
import dev.ridill.oar.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.util.Currency

class TransactionRepositoryImpl(
    private val db: OarDatabase,
    private val transactionDao: TransactionDao,
    private val schedulesRepo: SchedulesRepository
) : TransactionRepository {
    override fun getAllTransactionsPaged(
        query: String?,
        cycleIds: Set<Long>?,
        type: TransactionType?,
        showExcluded: Boolean,
        tagIds: Set<Long>?,
        folderId: Long?,
        currency: Currency?
    ): Flow<PagingData<TransactionEntry>> = Pager(
        config = PagingConfig(pageSize = UtilConstants.DEFAULT_PAGE_SIZE),
        pagingSourceFactory = {
            transactionDao.getTransactionsPaged(
                query = query,
                cycleIds = cycleIds,
                type = type,
                showExcluded = showExcluded,
                tagIds = tagIds?.takeIf { it.isNotEmpty() },
                folderId = folderId,
                currencyCode = currency?.currencyCode
            )
        }
    ).flow
        .mapLatest { it.map(TransactionDetailsView::toTransactionListItem) }

    override fun getDateSeparatedTransactions(
        query: String?,
        cycleIds: Set<Long>?,
        type: TransactionType?,
        showExcluded: Boolean,
        tagIds: Set<Long>?,
        folderId: Long?,
        currency: Currency?
    ): Flow<PagingData<TransactionListItemUIModel>> = getAllTransactionsPaged(
        query = query,
        cycleIds = cycleIds,
        type = type,
        showExcluded = showExcluded,
        tagIds = tagIds,
        folderId = folderId,
        currency = currency
    ).mapLatest { pagingData ->
        pagingData.map { TransactionListItemUIModel.TransactionItem(it) }
    }.mapLatest { pagingData ->
        pagingData
            .insertSeparators<TransactionListItemUIModel.TransactionItem, TransactionListItemUIModel>
            { before, after ->
                if (before?.cycleEntry?.id != after?.cycleEntry?.id) after?.cycleEntry
                    ?.let { TransactionListItemUIModel.CycleSeparator(it) }
                else null
            }
    }

    override suspend fun saveTransaction(
        cycleId: Long,
        amount: Double,
        id: Long,
        note: String?,
        timestamp: LocalDateTime,
        type: TransactionType,
        tagId: Long?,
        folderId: Long?,
        scheduleId: Long?,
        excluded: Boolean,
        currency: Currency?
    ): Transaction = withContext(Dispatchers.IO) {
        val currencyPref = currency ?: LocaleUtil.defaultCurrency
        val entity = TransactionEntity(
            id = id,
            note = note.orEmpty(),
            amount = amount,
            timestamp = timestamp,
            type = type,
            isExcluded = excluded,
            tagId = tagId,
            folderId = folderId,
            scheduleId = scheduleId,
            currencyCode = currencyPref.currencyCode,
            cycleId = cycleId
        )
        val insertedId = transactionDao.upsert(entity).first()
        entity.copy(id = insertedId)
            .toTransaction()
    }

    override suspend fun deleteSafely(
        id: Long
    ): Result<Unit, BasicError> = withContext(Dispatchers.IO) {
        try {
            db.withTransaction {
                val transaction = transactionDao.getTransactionById(id)
                    ?: throw TransactionNotFoundThrowable()
                transactionDao.delete(transaction)

                // If schedule ID is null, return out with Success
                val scheduleId = transaction.scheduleId
                    ?: return@withTransaction Result.Success(Unit)

                // Update last transaction date for schedule
                val schedule = schedulesRepo.getScheduleById(scheduleId)
                    ?: return@withTransaction Result.Success(Unit)

                val newLastPaymentTimestamp = schedulesRepo
                    .getLatestTxTimestampForSchedule(scheduleId)

                val newNextPaymentTimestamp = if (newLastPaymentTimestamp != null)
                    schedulesRepo.calculateNextPaymentTimestampFromDate(
                        newLastPaymentTimestamp,
                        schedule.repetition
                    )
                else schedule.lastPaymentTimestamp

                // update schedule and set new reminder for next date
                val updatedSchedule = schedule.copy(
                    lastPaymentTimestamp = newLastPaymentTimestamp,
                    nextPaymentTimestamp = newNextPaymentTimestamp
                )

                schedulesRepo.updateSchedules(updatedSchedule)
                Result.Success(Unit)
            }
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t, "deleteSafely")
            Result.Error(
                error = BasicError.UNKNOWN,
                message = UiText.StringResource(resId = R.string.error_unknown, isErrorText = true)
            )
        }
    }

    override suspend fun deleteSafely(
        ids: Set<Long>
    ): Result<Unit, BasicError> = withContext(Dispatchers.IO) {
        try {
            db.withTransaction {
                val transactions = transactionDao.getTransactionsByIds(ids)
                    .ifEmpty { throw TransactionNotFoundThrowable() }
                transactionDao.deleteMultipleTransactionsById(ids)
                val scheduleIds = transactions
                    .mapNotNull { it.scheduleId }
                    .toSet()

                val updatedSchedules = scheduleIds.map { scheduleId ->
                    async(Dispatchers.IO) {
                        // Update last transaction date for schedule
                        val schedule = schedulesRepo.getScheduleById(scheduleId)
                            ?: return@async null

                        val newLastPaymentTimestamp = schedulesRepo
                            .getLatestTxTimestampForSchedule(scheduleId)

                        val newNextPaymentTimestamp = if (newLastPaymentTimestamp != null)
                            schedulesRepo.calculateNextPaymentTimestampFromDate(
                                newLastPaymentTimestamp,
                                schedule.repetition
                            )
                        else schedule.lastPaymentTimestamp

                        // update schedule and set new reminder for next date
                        schedule.copy(
                            lastPaymentTimestamp = newLastPaymentTimestamp,
                            nextPaymentTimestamp = newNextPaymentTimestamp
                        )
                    }
                }.awaitAll()
                    .filterNotNull()
                schedulesRepo.updateSchedules(*updatedSchedules.toTypedArray())
                Result.Success(Unit)
            }
        } catch (t: Throwable) {
            t.rethrowIfCoroutineCancellation()
            logE(t, "deleteSafely")
            Result.Error(
                error = BasicError.UNKNOWN,
                message = UiText.StringResource(
                    resId = R.string.error_unknown,
                    isErrorText = true
                )
            )
        }
    }

    override suspend fun toggleExcluded(id: Long, excluded: Boolean) = withContext(Dispatchers.IO) {
        transactionDao.toggleExclusionByIds(setOf(id), excluded)
    }
}

class TransactionNotFoundThrowable : Throwable()