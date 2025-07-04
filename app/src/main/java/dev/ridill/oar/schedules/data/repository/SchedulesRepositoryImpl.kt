package dev.ridill.oar.schedules.data.repository

import androidx.room.withTransaction
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.data.util.trySuspend
import dev.ridill.oar.core.domain.service.ReceiverService
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.schedules.data.local.SchedulesDao
import dev.ridill.oar.schedules.data.local.entity.ScheduleEntity
import dev.ridill.oar.schedules.data.toEntity
import dev.ridill.oar.schedules.data.toSchedule
import dev.ridill.oar.schedules.domain.model.Schedule
import dev.ridill.oar.schedules.domain.model.ScheduleRepetition
import dev.ridill.oar.schedules.domain.repository.SchedulesRepository
import dev.ridill.oar.schedules.domain.scheduleReminder.ScheduleReminder
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class SchedulesRepositoryImpl(
    private val db: OarDatabase,
    private val schedulesDao: SchedulesDao,
    private val transactionDao: TransactionDao,
    private val scheduler: ScheduleReminder,
    private val receiverService: ReceiverService,
    private val cycleRepo: BudgetCycleRepository,
) : SchedulesRepository {
    override suspend fun getScheduleById(
        id: Long
    ): Schedule? = withContext(Dispatchers.IO) {
        schedulesDao.getScheduleById(id)?.toSchedule()
    }

    override fun calculateNextPaymentTimestampFromDate(
        dateTime: LocalDateTime,
        repetition: ScheduleRepetition
    ): LocalDateTime? = when (repetition) {
        ScheduleRepetition.NO_REPEAT -> null
        ScheduleRepetition.WEEKLY -> dateTime.plusWeeks(1)
        ScheduleRepetition.MONTHLY -> dateTime.plusMonths(1)
        ScheduleRepetition.BI_MONTHLY -> dateTime.plusMonths(2)
        ScheduleRepetition.YEARLY -> dateTime.plusYears(1)
    }

    override fun calculateLastPaymentTimestampFromDate(
        dateTime: LocalDateTime,
        repetition: ScheduleRepetition
    ): LocalDateTime? = when (repetition) {
        ScheduleRepetition.NO_REPEAT -> null
        ScheduleRepetition.WEEKLY -> dateTime.minusWeeks(1)
        ScheduleRepetition.MONTHLY -> dateTime.minusMonths(1)
        ScheduleRepetition.BI_MONTHLY -> dateTime.minusMonths(2)
        ScheduleRepetition.YEARLY -> dateTime.minusYears(1)
    }

    override suspend fun saveScheduleAndSetReminder(
        schedule: Schedule
    ) = withContext(Dispatchers.IO) {
        val insertedId = schedulesDao.upsert(schedule.toEntity()).first()
            .takeIf { it > OarDatabase.DEFAULT_ID_LONG }
            ?: schedule.id
        scheduleReminder(schedule.copy(id = insertedId))
    }

    override fun scheduleReminder(schedule: Schedule) {
        scheduler.cancel(schedule.id)
        scheduler.setReminder(schedule)
        receiverService.toggleBootAndTimeSetReceivers(true)
    }

    override suspend fun createTransactionFromScheduleAndSetNextReminder(
        schedule: Schedule,
        dateTime: LocalDateTime
    ) = withContext(Dispatchers.IO) {
        val activeCycle = cycleRepo.getActiveCycle()
        db.withTransaction {
            val transaction = TransactionEntity(
                amount = schedule.amount,
                note = schedule.note.orEmpty(),
                timestamp = dateTime,
                type = schedule.type,
                tagId = schedule.tagId,
                folderId = schedule.folderId,
                scheduleId = schedule.id,
                isExcluded = false,
                currencyCode = schedule.currency.currencyCode,
                cycleId = activeCycle?.id ?: OarDatabase.DEFAULT_ID_LONG
            )
            transactionDao.upsert(transaction)
            val nextReminderDate = schedule.nextPaymentTimestamp
                ?.let { calculateNextPaymentTimestampFromDate(it, schedule.repetition) }
            saveScheduleAndSetReminder(
                schedule = schedule.copy(
                    nextPaymentTimestamp = nextReminderDate,
                    lastPaymentTimestamp = dateTime
                )
            )
        }
    }

    override suspend fun getOldestTxTimestampForSchedule(
        id: Long
    ): LocalDateTime? = withContext(Dispatchers.IO) {
        schedulesDao.getMinTxTimestampForSchedule(id)
    }

    override suspend fun getLatestTxTimestampForSchedule(
        id: Long
    ): LocalDateTime? = withContext(Dispatchers.IO) {
        schedulesDao.getMaxTxTimestampForSchedule(id)
    }

    override suspend fun deleteScheduleById(id: Long) = withContext(Dispatchers.IO) {
        val entity = schedulesDao.getScheduleById(id) ?: return@withContext
        cancelSchedule(entity.toSchedule())
        schedulesDao.delete(entity)
    }

    override suspend fun cancelSchedule(schedule: Schedule) {
        scheduler.cancel(schedule.id)
    }

    override suspend fun setAllFutureScheduleReminders() = withContext(Dispatchers.IO) {
        schedulesDao.getAllSchedulesAfterTimestamp(DateUtil.now())
            .map(ScheduleEntity::toSchedule)
            .forEach(scheduler::setReminder)
    }

    override suspend fun deleteSchedulesByIds(ids: Set<Long>) {
        withContext(Dispatchers.IO) {
            trySuspend {
                ids.forEach { scheduler.cancel(it) }
                schedulesDao.deleteSchedulesById(ids)
            }
        }
    }

    override suspend fun updateSchedules(vararg schedule: Schedule) = withContext(Dispatchers.IO) {
        schedulesDao.update(*schedule.map(Schedule::toEntity).toTypedArray())
    }
}