package dev.ridill.oar.transactions.data.repository

import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.folders.domain.repository.FolderDetailsRepository
import dev.ridill.oar.schedules.domain.model.Schedule
import dev.ridill.oar.schedules.domain.model.ScheduleRepetition
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.toEntity
import dev.ridill.oar.transactions.data.toTransaction
import dev.ridill.oar.transactions.domain.model.Transaction
import dev.ridill.oar.transactions.domain.repository.AddEditTransactionRepository
import dev.ridill.oar.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import kotlin.math.roundToLong

class AddEditTransactionRepositoryImpl(
    private val dao: TransactionDao,
    private val repo: TransactionRepository,
    private val schedulesRepo: SchedulesRepository,
    private val folderRepo: FolderDetailsRepository
) : AddEditTransactionRepository {
    override suspend fun getTransactionById(
        id: Long
    ): Transaction? = withContext(Dispatchers.IO) {
        dao.getTransactionById(id)?.toTransaction()
    }

    override fun getAmountRecommendations(): Flow<List<Long>> = dao.getTransactionAmountRange()
        .mapLatest { (upperLimit, lowerLimit) ->
            val roundedUpper = ((upperLimit.roundToLong() / 10) * 10)
                .coerceAtLeast(RANGE_MIN_VALUE)
            val roundedLower = ((lowerLimit.roundToLong() / 10) * 10)
                .coerceAtLeast(RANGE_MIN_VALUE)

            val range = roundedUpper - roundedLower

            if (range == Long.Zero) buildList {
                repeat(3) {
                    add(RANGE_MIN_VALUE * (it + 1))
                }
            }
            else listOf(roundedLower, roundedLower + (range / 2), roundedUpper)
        }

    override suspend fun saveTransaction(
        transaction: Transaction
    ): Long = withContext(Dispatchers.IO) {
        dao.upsert(transaction.toEntity()).first()
    }

    override suspend fun deleteTransaction(id: Long) = repo.deleteById(id)

    override suspend fun toggleExclusionById(id: Long, excluded: Boolean) =
        withContext(Dispatchers.IO) {
            dao.toggleExclusionByIds(setOf(id), excluded)
        }

    override suspend fun getScheduleById(id: Long): Schedule? = schedulesRepo.getScheduleById(id)
        ?.let { schedule ->
            val nextPaymentTimestamp = schedule.nextPaymentTimestamp
                ?: schedule.lastPaymentTimestamp
                    ?.let {
                        schedulesRepo.calculateNextPaymentTimestampFromDate(
                            it,
                            schedule.repetition
                        )
                    }

            schedule.copy(
                nextPaymentTimestamp = nextPaymentTimestamp
            )
        }

    override suspend fun deleteSchedule(id: Long) =
        schedulesRepo.deleteScheduleById(id)

    override suspend fun saveAsSchedule(
        transaction: Transaction,
        repetition: ScheduleRepetition
    ) {
        val schedule = schedulesRepo.getScheduleById(transaction.id)
            ?.copy(
                amount = transaction.amount.toDoubleOrNull().orZero(),
                note = transaction.note.ifEmpty { null },
                type = transaction.type,
                repetition = repetition,
                tagId = transaction.tagId,
                folderId = transaction.folderId,
                nextPaymentTimestamp = transaction.timestamp,
                currency = transaction.currency
            ) ?: Schedule.fromTransaction(transaction, repetition)

        schedulesRepo.saveScheduleAndSetReminder(schedule)
    }

    override fun getFolderNameForId(id: Long?): Flow<String?> = folderRepo
        .getFolderDetailsById(id ?: OarDatabase.INVALID_ID_LONG)
        .mapLatest { it?.name }
        .distinctUntilChanged()
}

private const val RANGE_MIN_VALUE = 50L