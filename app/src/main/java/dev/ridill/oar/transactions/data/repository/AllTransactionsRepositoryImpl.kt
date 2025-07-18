package dev.ridill.oar.transactions.data.repository

import androidx.paging.PagingData
import androidx.room.withTransaction
import dev.ridill.oar.aggregations.data.local.AggregationsDao
import dev.ridill.oar.budgetCycles.domain.repository.BudgetCycleRepository
import dev.ridill.oar.core.data.db.OarDatabase
import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.domain.util.Empty
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.data.local.entity.TransactionEntity
import dev.ridill.oar.transactions.data.local.relation.AmountAndCurrencyRelation
import dev.ridill.oar.transactions.data.toAggregateAmountItem
import dev.ridill.oar.transactions.domain.model.AggregateAmountItem
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionListItemUIModel
import dev.ridill.oar.transactions.domain.model.TransactionType
import dev.ridill.oar.transactions.domain.repository.AllTransactionsRepository
import dev.ridill.oar.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.absoluteValue

class AllTransactionsRepositoryImpl(
    private val db: OarDatabase,
    private val transactionsDao: TransactionDao,
    private val aggregationsDao: AggregationsDao,
    private val cycleRepo: BudgetCycleRepository,
    private val repo: TransactionRepository,
    private val preferencesManager: PreferencesManager,
) : AllTransactionsRepository {
    override fun getAmountAggregate(
        dateRange: Pair<LocalDate, LocalDate>?,
        type: TransactionType?,
        addExcluded: Boolean,
        tagIds: Set<Long>,
        selectedTxIds: Set<Long>
    ): Flow<List<AggregateAmountItem>> = aggregationsDao.getAggregatesGroupedByCurrencyCode(
        type = type,
        tagIds = tagIds.takeIf { it.isNotEmpty() },
        addExcluded = addExcluded,
        selectedTxIds = selectedTxIds.takeIf { it.isNotEmpty() },
        currencyCode = null,
        cycleIds = null
    ).mapLatest { it.map(AmountAndCurrencyRelation::toAggregateAmountItem) }
        .distinctUntilChanged()

    override fun getDateLimits(): Flow<Pair<LocalDate, LocalDate>> = transactionsDao.getDateLimits()
        .mapLatest { limits -> limits.minDate to limits.maxDate }
        .distinctUntilChanged()

    override fun getAllTransactionsPaged(
        dateRange: Pair<LocalDate, LocalDate>?,
        transactionType: TransactionType?,
        showExcluded: Boolean,
        tagIds: Set<Long>?,
        folderId: Long?
    ): Flow<PagingData<TransactionListItemUIModel>> = repo.getDateSeparatedTransactions(
        cycleIds = null,
        type = transactionType,
        showExcluded = showExcluded,
        tagIds = tagIds,
        folderId = folderId
    )

    override fun getSearchResults(query: String?): Flow<PagingData<TransactionEntry>> = repo
        .getAllTransactionsPaged(query = query)

    override suspend fun setTagIdToTransactions(
        tagId: Long?,
        transactionIds: Set<Long>
    ) = withContext(Dispatchers.IO) {
        transactionsDao.setTagIdToTransactionsByIds(tagId = tagId, ids = transactionIds)
    }

    override fun getShowExcludedOption(): Flow<Boolean> = preferencesManager.preferences
        .mapLatest { it.allTransactionsShowExcludedOption }
        .distinctUntilChanged()

    override suspend fun toggleShowExcludedOption(show: Boolean) =
        preferencesManager.updateAllTransactionsShowExcludedOption(show)

    override suspend fun toggleTransactionExclusionByIds(ids: Set<Long>, excluded: Boolean) =
        withContext(Dispatchers.IO) {
            transactionsDao.toggleExclusionByIds(ids, excluded)
        }

    override suspend fun deleteTransactionsByIds(ids: Set<Long>) =
        repo.deleteById(*(ids.toLongArray()))

    override suspend fun addTransactionsToFolderByIds(
        ids: Set<Long>,
        folderId: Long
    ) = withContext(Dispatchers.IO) {
        transactionsDao.setFolderIdToTransactionsByIds(ids = ids, folderId = folderId)
    }

    override suspend fun removeTransactionsFromFolders(ids: Set<Long>) =
        withContext(Dispatchers.IO) {
            transactionsDao.removeFolderFromTransactionsByIds(ids)
        }

    override suspend fun aggregateTogether(
        ids: Set<Long>,
        dateTime: LocalDateTime
    ): Long = withContext(Dispatchers.IO) {
        db.withTransaction {
            val currentCurrencyPref = cycleRepo.getActiveCycleFlow().first()?.currency
                ?: LocaleUtil.defaultCurrency
            val aggregatedAmount = aggregationsDao.getAggregateAmountForCycle(-1L)
            var insertedId = -1L
            if (aggregatedAmount != Double.Zero) {
                val type = if (aggregatedAmount > 0) TransactionType.DEBIT
                else TransactionType.CREDIT
                val entity = TransactionEntity(
                    note = String.Empty,
                    amount = aggregatedAmount.absoluteValue,
                    timestamp = dateTime,
                    type = type,
                    isExcluded = false,
                    tagId = null,
                    folderId = null,
                    scheduleId = null,
                    currencyCode = currentCurrencyPref.currencyCode,
                    cycleId = OarDatabase.INVALID_ID_LONG
                )
                insertedId = transactionsDao.upsert(entity).first()
            }
            transactionsDao.deleteMultipleTransactionsById(ids)
            insertedId
        }
    }
}