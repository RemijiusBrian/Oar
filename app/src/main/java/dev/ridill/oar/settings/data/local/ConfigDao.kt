package dev.ridill.oar.settings.data.local

import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.settings.data.local.entity.ConfigEntity

@Dao
interface ConfigDao : BaseDao<ConfigEntity> {
    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.BACKUP_INTERVAL}'")
    suspend fun getBackupInterval(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.CYCLE_BUDGET_AMOUNT}'")
    suspend fun getCycleBudget(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.CYCLE_CURRENCY_CODE}'")
    suspend fun getCycleCurrencyCode(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.CYCLE_START_DAY_TYPE}'")
    suspend fun getCycleStartDayType(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.CYCLE_DURATION}'")
    suspend fun getCycleDuration(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKey.CYCLE_DURATION_UNIT}'")
    suspend fun getCycleDurationUnit(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = :key")
    suspend fun getValueForKey(key: String): String?
}