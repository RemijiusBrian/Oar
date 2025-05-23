package dev.ridill.oar.budgetCycles.domain.cycleManager

import android.content.Context
import java.time.LocalDateTime

interface CycleManager {
    companion object {
        fun action(context: Context): String = "${context.packageName}.budgetCycle.CYCLE_COMPLETE"
        const val EXTRA_CYCLE_ID = "EXTRA_CYCLE_ID"
    }

    fun scheduleCycleCompletion(cycleId: Long, endDate: LocalDateTime)
}