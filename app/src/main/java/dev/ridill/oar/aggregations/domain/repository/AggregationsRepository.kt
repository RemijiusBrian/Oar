package dev.ridill.oar.aggregations.domain.repository

import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface AggregationsRepository {
    fun getAmountAggregate(
        cycleIds: Set<Long>? = null,
        selectedTxIds: Set<Long>? = null,
        type: TransactionType? = null,
        tagIds: Set<Long>? = null,
        currency: Currency? = null,
        addExcluded: Boolean = false
    ): Flow<List<AggregateAmountItem>>

    fun getTotalDebitsForCycle(id: Long, currency: Currency): Flow<Double>
    fun getTotalCreditsForCycle(id: Long, currency: Currency): Flow<Double>
    suspend fun getAggregateAmountForCycle(id: Long): Double
}