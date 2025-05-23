package dev.ridill.oar.settings.domain.repositoty

import dev.ridill.oar.core.domain.util.DateUtil
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface BudgetPreferenceRepository {
    fun getBudgetPreferenceForMonth(
        date: LocalDate = DateUtil.dateNow()
    ): Flow<Long>

    suspend fun saveBudgetPreference(
        amount: Long,
        date: LocalDate = DateUtil.dateNow()
    )
}