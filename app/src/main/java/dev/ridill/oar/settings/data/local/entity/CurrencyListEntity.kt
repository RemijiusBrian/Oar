package dev.ridill.oar.settings.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency_list_table")
data class CurrencyListEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "currency_code")
    val currencyCode: String,

    @ColumnInfo(name = "display_name")
    val displayName: String
)