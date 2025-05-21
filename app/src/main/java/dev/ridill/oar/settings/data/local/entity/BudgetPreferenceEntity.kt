package dev.ridill.oar.settings.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "budget_preference_table")
data class BudgetPreferenceEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo("date")
    val date: LocalDate,

    @ColumnInfo("amount")
    val amount: Long
)