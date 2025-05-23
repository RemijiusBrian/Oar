package dev.ridill.oar.dashboard.presentation

import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.schedules.domain.model.ActiveSchedule

data class DashboardState(
    val balance: Double = Double.Zero,
    val spentAmount: Double = Double.Zero,
    val usagePercent: Float = Float.Zero,
    val monthlyBudgetInclCredits: Double = Double.Zero,
    val activeSchedules: List<ActiveSchedule> = emptyList(),
    val signedInUser: UserAccount? = null,
)