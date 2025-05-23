package dev.ridill.oar.budgetCycles.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.ridill.oar.budgetCycles.domain.model.CycleStatus
import dev.ridill.oar.core.data.db.OarDatabase
import java.time.LocalDate

@Entity(
    tableName = "budget_cycle_table",
    indices = [
        Index("start_date", "end_date", unique = true),
        Index("status")
    ]
)
data class BudgetCycleEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = OarDatabase.DEFAULT_ID_LONG,

    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,

    @ColumnInfo(name = "end_date")
    val endDate: LocalDate,

    @ColumnInfo(name = "budget")
    val budget: Double,

    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "status")
    val status: CycleStatus
)