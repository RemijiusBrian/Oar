package dev.ridill.oar.schedules.domain.scheduleReminder

import dev.ridill.oar.schedules.domain.model.Schedule

interface ScheduleReminder {
    fun setReminder(schedule: Schedule)
    fun cancel(id: Long)

    companion object {
        const val ACTION = "dev.ridill.oar.SCHEDULE_REMINDER"
        const val EXTRA_SCHEDULE_ID = "EXTRA_SCHEDULE_ID"
    }
}