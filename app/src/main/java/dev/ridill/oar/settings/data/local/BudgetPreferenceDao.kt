package dev.ridill.oar.settings.data.local

import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.settings.data.local.entity.BudgetPreferenceEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BudgetPreferenceDao : BaseDao<BudgetPreferenceEntity> {
    @Query(
        """
        SELECT IFNULL(amount, 0)
        FROM budget_preference_table
        WHERE DATE(date) <= DATE(:date)
        ORDER BY DATE(date) DESC
        LIMIT 1
    """
    )
    fun getAmountForDateOrLast(date: LocalDate): Flow<Long>
}