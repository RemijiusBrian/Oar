package dev.ridill.oar.aggregations.domain.repository

import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface AggregationsRepository {
    fun getAmountAggregate(
        cycleIds: Set<Long>?,
        selectedTxIds: Set<Long>?,
        type: TransactionType?,
        tagIds: Set<Long>?,
        currency: Currency?,
        addExcluded: Boolean
    ): Flow<List<AggregateAmountItem>>

    fun getTotalDebitsForCycle(id: Long, currency: Currency): Flow<Double>
    fun getTotalCreditsForCycle(id: Long, currency: Currency): Flow<Double>
    suspend fun getAggregateAmountForCycle(id: Long): Double
}