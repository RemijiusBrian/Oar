package dev.ridill.oar.folders.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.folders.data.local.entity.FolderEntity
import dev.ridill.oar.folders.data.local.views.FolderAndAggregateView
import kotlinx.coroutines.flow.Flow

@Dao
interface FolderDao : BaseDao<FolderEntity> {
    @Transaction
    @Query(
        """SELECT * FROM folder_and_aggregate_view
        ORDER BY CASE
                WHEN aggregate > 0.0 THEN 1
                WHEN aggregate < 0.0 THEN 0
                WHEN aggregate = 0.0 THEN -1
            END DESC,
            name ASC, DATETIME(createdTimestamp) DESC
        """
    )
    fun getFolderAndAggregatesPaged(): PagingSource<Int, FolderAndAggregateView>

    @Transaction
    @Query("SELECT * FROM folder_and_aggregate_view WHERE id = :id")
    fun getFolderAndAggregateById(id: Long): Flow<FolderAndAggregateView?>

    @Query("SELECT * FROM folder_table WHERE name LIKE '%' || :query || '%' ORDER BY name ASC, DATETIME(created_timestamp) DESC")
    fun getFoldersPaged(query: String): PagingSource<Int, FolderEntity>

    @Query("SELECT * FROM folder_table WHERE id = :id")
    suspend fun getFolderById(id: Long): FolderEntity?

    @Query("SELECT * FROM folder_table WHERE id = :id")
    fun getFolderByIdFlow(id: Long): Flow<FolderEntity?>

    @Transaction
    suspend fun deleteFolderOnlyById(id: Long) {
        removeTransactionsFromFolderById(id)
        deleteFolderById(id)
    }

    @Transaction
    suspend fun deleteFolderAndTransactionsById(id: Long) {
        deleteTransactionsByFolderId(id)
        deleteFolderById(id)
    }

    @Query("DELETE FROM folder_table WHERE id = :id")
    suspend fun deleteFolderById(id: Long)

    @Query("DELETE FROM transaction_table WHERE folder_id = :folderId")
    suspend fun deleteTransactionsByFolderId(folderId: Long)

    @Query("UPDATE transaction_table SET folder_id = NULL WHERE folder_id = :folderId")
    suspend fun removeTransactionsFromFolderById(folderId: Long)
}