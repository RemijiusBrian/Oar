package dev.ridill.oar.schedules.data

import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.schedules.data.local.entity.ScheduleEntity
import dev.ridill.oar.schedules.domain.model.ActiveSchedule
import dev.ridill.oar.schedules.domain.model.Schedule
import dev.ridill.oar.transactions.domain.model.Transaction
import java.time.LocalDateTime

fun ScheduleEntity.toSchedule(): Schedule = Schedule(
    id = id,
    repetition = repetition,
    nextPaymentTimestamp = nextPaymentTimestamp,
    amount = amount,
    note = note,
    type = type,
    tagId = tagId,
    folderId = folderId,
    lastPaymentTimestamp = lastPaymentTimestamp,
    currency = LocaleUtil.currencyForCode(currencyCode)
)

fun Schedule.toTransaction(
    dateTime: LocalDateTime = DateUtil.now(),
    txId: Long = OarDatabase.DEFAULT_ID_LONG
): Transaction = Transaction(
    id = txId,
    amount = amount.toString(),
    note = note.orEmpty(),
    timestamp = dateTime,
    type = type,
    tagId = tagId,
    folderId = folderId,
    excluded = false,
    scheduleId = id,
    currency = currency
)

fun Schedule.toEntity(): ScheduleEntity = ScheduleEntity(
    id = id,
    amount = amount,
    note = note,
    type = type,
    repetition = repetition,
    tagId = tagId,
    folderId = folderId,
    nextPaymentTimestamp = nextPaymentTimestamp,
    lastPaymentTimestamp = lastPaymentTimestamp,
    currencyCode = currency.currencyCode
)

fun ScheduleEntity.toActiveSchedule(): ActiveSchedule = ActiveSchedule(
    id = id,
    note = note,
    amount = amount,
    currency = LocaleUtil.currencyForCode(currencyCode),
    type = type,
    nextPaymentDateTime = nextPaymentTimestamp!!
)