package dev.ridill.oar.transactions.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import dev.ridill.oar.core.data.db.BaseDao
import dev.ridill.oar.transactions.data.local.entity.TransactionEntity
import dev.ridill.oar.transactions.data.local.views.TransactionDetailsView
import dev.ridill.oar.transactions.domain.model.TransactionAmountLimits
import dev.ridill.oar.transactions.domain.model.TransactionDateLimits
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao : BaseDao<TransactionEntity> {

    @Query(
        """
        SELECT IFNULL(MAX(amount), 0.0) AS upperLimit, IFNULL(MIN(amount), 0.0) AS lowerLimit
        FROM transaction_table
    """
    )
    fun getTransactionAmountRange(): Flow<TransactionAmountLimits>

    @Query(
        """
        SELECT IFNULL(MAX(DATE(timestamp)), DATE('now')) AS maxDate, IFNULL(MIN(DATE(timestamp)), DATE('now')) AS minDate
        FROM transaction_table
    """
    )
    fun getDateLimits(): Flow<TransactionDateLimits>

    @Query("SELECT * FROM transaction_table WHERE id = :id")
    suspend fun getTransactionById(id: Long): TransactionEntity?

    @Query("SELECT * FROM transaction_table WHERE id IN (:ids)")
    suspend fun getTransactionsByIds(ids: Set<Long>): List<TransactionEntity>

    @Transaction
    @Query(
        """
        SELECT * FROM transaction_details_view
        WHERE (:query IS NOT NULL AND (transactionAmount LIKE :query || '%' OR transactionNote LIKE '%' || :query || '%' OR tagName LIKE '%' || :query || '%' OR folderName LIKE '%' || :query || '%'))
            AND (COALESCE(:cycleIds, 0) = 0 OR cycleId IN (:cycleIds))
            AND (:type IS NULL OR transactionType = :type)
            AND (COALESCE(:tagIds, 0) = 0 OR tagId IN (:tagIds))
            AND (:folderId IS NULL OR folderId = :folderId)
            AND (:showExcluded = 1 OR excluded = 0)
            AND (:currencyCode IS NULL OR currencyCode = :currencyCode)
        ORDER BY DATE(cycleStartDate) DESC, DATE(cycleEndDate) DESC, DATETIME(transactionTimestamp) DESC, transactionNote DESC, tagName DESC, folderName DESC
        """
    )
    fun getTransactionsPaged(
        query: String?,
        cycleIds: Set<Long>?,
        type: TransactionType?,
        showExcluded: Boolean,
        tagIds: Set<Long>?,
        folderId: Long?,
        currencyCode: String?
    ): PagingSource<Int, TransactionDetailsView>

    @Query("UPDATE transaction_table SET tag_id = :tagId WHERE id IN (:ids)")
    suspend fun setTagIdToTransactionsByIds(tagId: Long?, ids: Set<Long>)

    @Query("UPDATE transaction_table SET is_excluded = :exclude WHERE id IN (:ids)")
    suspend fun toggleExclusionByIds(ids: Set<Long>, exclude: Boolean)

    @Query("DELETE FROM transaction_table WHERE id IN (:ids)")
    suspend fun deleteMultipleTransactionsById(ids: Set<Long>)

    @Query("UPDATE transaction_table SET folder_id = :folderId WHERE id IN (:ids)")
    suspend fun setFolderIdToTransactionsByIds(ids: Set<Long>, folderId: Long?)

    @Query("UPDATE transaction_table SET folder_id = NULL WHERE id IN (:ids)")
    suspend fun removeFolderFromTransactionsByIds(ids: Set<Long>)
}