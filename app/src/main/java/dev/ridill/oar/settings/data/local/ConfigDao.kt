package dev.ridill.oar.settings.data.local

import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.settings.data.local.entity.ConfigEntity

@Dao
interface ConfigDao : BaseDao<ConfigEntity> {
    @Query("SELECT config_value FROM config_table WHERE config_key = '${ConfigKeys.BACKUP_INTERVAL}'")
    suspend fun getBackupInterval(): String?
}