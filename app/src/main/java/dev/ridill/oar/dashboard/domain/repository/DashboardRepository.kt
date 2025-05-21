package dev.ridill.oar.dashboard.domain.repository

import androidx.paging.PagingData
import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.schedules.domain.model.ActiveSchedule
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import kotlinx.coroutines.flow.Flow

interface DashboardRepository {
    fun refreshCurrentDate()
    fun getSignedInUser(): Flow<UserAccount?>
    fun getCurrentBudget(): Flow<Long>
    fun getTotalDebitsForCurrentMonth(): Flow<Double>
    fun getTotalCreditsForCurrentMonth(): Flow<Double>
    fun getSchedulesActiveThisMonth(): Flow<List<ActiveSchedule>>
    fun getRecentSpends(): Flow<PagingData<TransactionEntry>>
}