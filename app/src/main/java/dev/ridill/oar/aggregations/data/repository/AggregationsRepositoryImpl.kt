package dev.ridill.oar.aggregations.data.repository

import dev.ridill.oar.aggregations.data.local.AggregationsDao
import dev.ridill.oar.aggregations.domain.repository.AggregationsRepository
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.transactions.data.local.relation.AmountAndCurrencyRelation
import dev.ridill.oar.transactions.data.toAggregateAmountItem
import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import dev.ridill.oar.transactions.domain.model.TransactionType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.util.Currency
import kotlin.math.absoluteValue

class AggregationsRepositoryImpl(
    private val dao: AggregationsDao
) : AggregationsRepository {
    override fun getAmountAggregate(
        cycleIds: Set<Long>?,
        selectedTxIds: Set<Long>?,
        type: TransactionType?,
        tagIds: Set<Long>?,
        currency: Currency?,
        addExcluded: Boolean
    ): Flow<List<AggregateAmountItem>> = dao.getAggregatesGroupedByCurrencyCode(
        cycleIds = cycleIds,
        selectedTxIds = selectedTxIds.takeIf { !it.isNullOrEmpty() },
        type = type,
        tagIds = tagIds.takeIf { !it.isNullOrEmpty() },
        currencyCode = null,
        addExcluded = addExcluded
    ).mapLatest { it.map(AmountAndCurrencyRelation::toAggregateAmountItem) }
        .distinctUntilChanged()

    override fun getTotalDebitsForCycle(
        id: Long,
        currency: Currency
    ): Flow<Double> = getAmountAggregate(
        cycleIds = setOf(id),
        currency = currency,
        selectedTxIds = null,
        type = TransactionType.DEBIT,
        addExcluded = false,
        tagIds = null
    ).mapLatest { it.firstOrNull() }
        .mapLatest { it?.amount.orZero() }
        .mapLatest { it.absoluteValue }
        .distinctUntilChanged()

    override fun getTotalCreditsForCycle(
        id: Long,
        currency: Currency
    ): Flow<Double> = getAmountAggregate(
        cycleIds = setOf(id),
        currency = currency,
        selectedTxIds = null,
        type = TransactionType.CREDIT,
        addExcluded = false,
        tagIds = null
    ).mapLatest { it.firstOrNull() }
        .mapLatest { it?.amount.orZero() }
        .mapLatest { it.absoluteValue }
        .distinctUntilChanged()

    override suspend fun getAggregateAmountForCycle(id: Long): Double =
        withContext(Dispatchers.IO) {
            dao.getAggregateAmountForCycle(id)
        }
}