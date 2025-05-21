package dev.ridill.oar.core.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Update
import androidx.room.Upsert

@Dao
interface BaseDao<T> {

    @Upsert
    suspend fun upsert(vararg entities: T): List<Long>

    @Delete
    suspend fun delete(vararg entities: T)

    @Update
    suspend fun update(vararg entities: T)
}