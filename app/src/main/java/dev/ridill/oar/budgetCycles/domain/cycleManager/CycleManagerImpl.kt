package dev.ridill.oar.budgetCycles.domain.cycleManager

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import dev.ridill.oar.core.domain.util.BuildUtil
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.logD
import dev.ridill.oar.core.domain.util.logE
import java.time.LocalDateTime

class CycleManagerImpl(
    private val context: Context
) : CycleManager {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    override fun scheduleCycleCompletion(cycleId: Long, endDateTime: LocalDateTime) {
        if (
            BuildUtil.isApiLevelAtLeast31
            && !alarmManager.canScheduleExactAlarms()
        ) return

        try {
            val endTimeMillis = DateUtil.toMillis(endDateTime)

            val intent = Intent(context, CycleCompletionReceiver::class.java).apply {
                this.action = CycleManager.action(context)
                putExtra(CycleManager.EXTRA_CYCLE_ID, cycleId)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                cycleId.hashCode(),
                intent,
                UtilConstants.pendingIntentFlags
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC,
                endTimeMillis,
                pendingIntent
            )

            logD("CycleManager") { "Scheduled cycle ID = $cycleId to complete at $endDateTime" }
        } catch (t: Throwable) {
            logE(t, "CycleManager")
        }
    }
}