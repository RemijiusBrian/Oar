package dev.ridill.oar.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.ridill.oar.folders.data.local.FolderDao
import dev.ridill.oar.folders.data.local.entity.FolderEntity
import dev.ridill.oar.folders.data.local.views.FolderAndAggregateView
import dev.ridill.oar.schedules.data.local.SchedulesDao
import dev.ridill.oar.schedules.data.local.entity.ScheduleEntity
import dev.ridill.oar.settings.data.local.BudgetPreferenceDao
import dev.ridill.oar.settings.data.local.ConfigDao
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.local.CurrencyPreferenceDao
import dev.ridill.oar.settings.data.local.entity.BudgetPreferenceEntity
import dev.ridill.oar.settings.data.local.entity.ConfigEntity
import dev.ridill.oar.settings.data.local.entity.CurrencyListEntity
import dev.ridill.oar.settings.data.local.entity.CurrencyPreferenceEntity
import dev.ridill.oar.tags.data.local.TagsDao
import dev.ridill.oar.tags.data.local.entity.TagEntity
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.local.entity.TransactionEntity
import dev.ridill.oar.transactions.data.local.views.TransactionDetailsView

@Database(
    entities = [
        BudgetPreferenceEntity::class,
        TransactionEntity::class,
        TagEntity::class,
        FolderEntity::class,
        ScheduleEntity::class,
        CurrencyListEntity::class,
        CurrencyPreferenceEntity::class,
        ConfigEntity::class
    ],
    views = [
        TransactionDetailsView::class,
        FolderAndAggregateView::class
    ],
    version = 1
)
@TypeConverters(DateTimeConverter::class)
abstract class OarDatabase : RoomDatabase() {
    companion object {
        const val NAME = "Rivo.db"
        const val DEFAULT_ID_LONG = 0L
        const val INVALID_LIMIT = -1
    }

    // Dao Methods
    abstract fun budgetPreferenceDao(): BudgetPreferenceDao
    abstract fun transactionDao(): TransactionDao
    abstract fun tagsDao(): TagsDao
    abstract fun folderDao(): FolderDao
    abstract fun schedulesDao(): SchedulesDao
    abstract fun currencyListDao(): CurrencyListDao
    abstract fun currencyPreferenceDao(): CurrencyPreferenceDao
    abstract fun configDao(): ConfigDao
}