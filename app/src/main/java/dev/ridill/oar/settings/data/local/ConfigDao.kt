package dev.ridill.oar.settings.data.local

import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.settings.data.local.entity.ConfigEntity

@Dao
interface ConfigDao : BaseDao<ConfigEntity> {
    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKeys.BACKUP_INTERVAL}'")
    suspend fun getBackupInterval(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKeys.CYCLE_BUDGET_AMOUNT}'")
    suspend fun getBudgetAmount(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKeys.CYCLE_BUDGET_CURRENCY_CODE}'")
    suspend fun getBudgetCurrencyCode(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKeys.BUDGET_CYCLE_START_DAY_TYPE}'")
    suspend fun getBudgetStartDayType(): String?

    @Query("SELECT config_value FROM config_table WHERE config_key = :key")
    suspend fun getValueForKey(key: String): String?
}