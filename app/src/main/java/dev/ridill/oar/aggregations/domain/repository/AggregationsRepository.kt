package dev.ridill.oar.aggregations.domain.repository

import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface AggregationsRepository {
    fun getAmountAggregateForTransactions(
        selectedTxIds: Set<Long>,
        addExcluded: Boolean = false
    ): Flow<List<AggregateAmountItem>>

    fun getAmountAggregateForCycle(
        cycleId: Long,
        currency: Currency? = null,
        addExcluded: Boolean = false,
        type: TransactionType? = null
    ): Flow<List<AggregateAmountItem>>

    fun getTotalDebitsForCycle(id: Long, currency: Currency): Flow<Double>
    fun getTotalCreditsForCycle(id: Long, currency: Currency): Flow<Double>
    suspend fun getAggregateAmountForCycle(id: Long): Double
}