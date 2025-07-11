package dev.ridill.oar.budgetCycles.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import dev.ridill.oar.budgetCycles.data.local.entity.BudgetCycleEntity
import dev.ridill.oar.budgetCycles.data.local.view.BudgetCycleDetailsView
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.settings.data.local.ConfigKey
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface BudgetCycleDao : BaseDao<BudgetCycleEntity> {

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM budget_cycle_details_view ORDER BY DATE(endDate) DESC LIMIT 1")
    suspend fun getLastCycle(): BudgetCycleDetailsView?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM budget_cycle_details_view WHERE active = 1 LIMIT 1")
    fun getActiveCycleFlow(): Flow<BudgetCycleDetailsView?>

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM budget_cycle_details_view WHERE active = 1 LIMIT 1")
    fun getActiveCycle(): BudgetCycleDetailsView?

    @Query("SELECT * FROM budget_cycle_table WHERE DATE(start_date) = DATE(:startDate) AND DATE(end_date) = DATE(:endDate)")
    suspend fun getCycleForDate(
        startDate: LocalDate,
        endDate: LocalDate
    ): BudgetCycleEntity?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM budget_cycle_details_view WHERE id = :id")
    suspend fun getCycleById(id: Long): BudgetCycleDetailsView?

    @RewriteQueriesToDropUnusedColumns
    @Query("SELECT * FROM budget_cycle_details_view WHERE id = :id")
    fun getCycleByIdFlow(id: Long): Flow<BudgetCycleDetailsView?>

    @Query(
        """
        UPDATE budget_cycle_table
        SET budget = :budget
        WHERE id = (SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.ACTIVE_CYCLE_ID}')
        """
    )
    suspend fun updateBudgetForActiveCycle(budget: Long)

    @Query(
        """
        UPDATE budget_cycle_table
        SET currency_code = :currencyCode
        WHERE id = (SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.ACTIVE_CYCLE_ID}')
        """
    )
    suspend fun updateCurrencyCodeForActiveCycle(currencyCode: String)
}