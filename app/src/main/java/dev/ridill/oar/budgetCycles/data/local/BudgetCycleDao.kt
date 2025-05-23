package dev.ridill.oar.budgetCycles.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.budgetCycles.data.local.entity.BudgetCycleEntity
import dev.ridill.oar.core.data.db.BaseDao
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetCycleDao : BaseDao<BudgetCycleEntity> {

    @Query("SELECT * FROM budget_cycle_table ORDER BY DATE(end_date) DESC LIMIT 1")
    suspend fun getLastCycle(): BudgetCycleEntity?

    @Query("SELECT * FROM budget_cycle_table WHERE status = 'ACTIVE'")
    fun getActiveCycle(): Flow<BudgetCycleEntity?>

    @Query("SELECT * FROM budget_cycle_table WHERE id = :id")
    suspend fun getCycleById(id: Long): BudgetCycleEntity?

    @Query("UPDATE budget_cycle_table SET status = 'COMPLETED' WHERE id = :id")
    suspend fun markCycleCompleted(id: Long)

    @Query("SELECT IFNULL(budget, 0.0) FROM budget_cycle_table WHERE status = 'ACTIVE'")
    fun getBudgetForActiveCycle(): Flow<Double>

    @Query("SELECT * FROM budget_cycle_table WHERE status = 'COMPLETED' ORDER BY start_date DESC")
    fun getCompletedCycleHistory(): PagingSource<Int, BudgetCycleEntity>

    @Query("UPDATE budget_cycle_table SET budget = :budget WHERE status = 'ACTIVE'")
    suspend fun updateBudgetForActiveCycle(budget: Double)
}