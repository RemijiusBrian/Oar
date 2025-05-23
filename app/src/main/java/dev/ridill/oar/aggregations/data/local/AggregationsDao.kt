package dev.ridill.oar.aggregations.data.local

import androidx.room.Dao
import androidx.room.Query
import dev.ridill.oar.transactions.data.local.relation.AmountAndCurrencyRelation
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface AggregationsDao {

    @Query(
        """
        SELECT currencyCode, IFNULL(SUM(
            CASE
                WHEN transactionType = 'DEBIT' THEN transactionAmount
                WHEN transactionType = 'CREDIT' THEN -transactionAmount
            END
        ), 0) as amount
        FROM transaction_details_view
        WHERE (COALESCE(:cycleIds, 0) = 0 OR cycleId IN (:cycleIds))
            AND (COALESCE(:selectedTxIds, 0) = 0 OR transactionId IN (:selectedTxIds))
            AND (:type IS NULL OR transactionType = :type)
            AND (COALESCE(:tagIds, 0) = 0 OR tagId IN (:tagIds))
            AND (:currencyCode IS NULL OR currencyCode = :currencyCode)
            AND (:addExcluded = 1 OR excluded = 0)
        GROUP BY currencyCode
    """
    )
    fun getAggregatesGroupedByCurrencyCode(
        cycleIds: Set<Long>?,
        selectedTxIds: Set<Long>?,
        type: TransactionType?,
        tagIds: Set<Long>?,
        currencyCode: String?,
        addExcluded: Boolean
    ): Flow<List<AmountAndCurrencyRelation>>

    @Query(
        """
         SELECT IFNULL(SUM(
            CASE
                WHEN type = 'DEBIT' THEN amount
                WHEN type = 'CREDIT' THEN -amount
            END
        ), 0) as amount
        FROM transaction_table WHERE cycle_id = :cycleId
    """
    )
    suspend fun getAggregateAmountForCycle(cycleId: Long): Double
}