package dev.ridill.oar.dashboard.data.repository

import androidx.paging.PagingData
import com.zhuinden.flowcombinetuplekt.combineTuple
import dev.ridill.oar.account.domain.model.AuthState
import dev.ridill.oar.account.domain.model.UserAccount
import dev.ridill.oar.account.domain.repository.AuthRepository
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.orZero
import dev.ridill.oar.dashboard.domain.repository.DashboardRepository
import dev.ridill.oar.schedules.data.local.SchedulesDao
import dev.ridill.oar.schedules.data.local.entity.ScheduleEntity
import dev.ridill.oar.schedules.data.toActiveSchedule
import dev.ridill.oar.schedules.domain.model.ActiveSchedule
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import dev.ridill.oar.transactions.data.local.TransactionDao
import dev.ridill.oar.transactions.domain.model.TransactionEntry
import dev.ridill.oar.transactions.domain.model.TransactionType
import dev.ridill.oar.transactions.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import java.time.temporal.TemporalAdjusters
import java.util.Currency
import kotlin.math.absoluteValue

class DashboardRepositoryImpl(
    private val currencyRepo: CurrencyRepository,
    private val authRepo: AuthRepository,
    private val budgetRepo: BudgetPreferenceRepository,
    private val transactionDao: TransactionDao,
    private val transactionRepo: TransactionRepository,
    private val schedulesDao: SchedulesDao
) : DashboardRepository {

    private val _currentDate = MutableStateFlow(DateUtil.dateNow())
    private val currentDate = _currentDate.asStateFlow()

    override fun refreshCurrentDate() {
        _currentDate.update { DateUtil.dateNow() }
    }

    private fun getCurrencyForCurrentMonth(): Flow<Currency> = currentDate.flatMapLatest {
        currencyRepo.getCurrencyPreferenceForMonth(it)
    }

    override fun getSignedInUser(): Flow<UserAccount?> = authRepo.getAuthState()
        .mapLatest { state ->
            when (state) {
                is AuthState.Authenticated -> state.account
                AuthState.UnAuthenticated -> null
            }
        }.distinctUntilChanged()

    override fun getCurrentBudget(): Flow<Long> = currentDate
        .flatMapLatest {
            budgetRepo.getBudgetPreferenceForMonth(it)
        }.distinctUntilChanged()

    override fun getTotalDebitsForCurrentMonth(): Flow<Double> = combineTuple(
        currentDate,
        getCurrencyForCurrentMonth()
    ).flatMapLatest { (date, currency) ->
        transactionDao.getAmountAggregate(
            startDate = date.withDayOfMonth(1),
            endDate = date.with(TemporalAdjusters.lastDayOfMonth()),
            type = TransactionType.DEBIT,
            tagIds = null,
            showExcluded = false,
            selectedTxIds = null,
            currencyCode = currency.currencyCode
        )
    }
        .mapLatest { it.firstOrNull() }
        .mapLatest { it?.amount.orZero() }
        .mapLatest { it.absoluteValue }
        .distinctUntilChanged()

    override fun getTotalCreditsForCurrentMonth(): Flow<Double> = combineTuple(
        currentDate,
        getCurrencyForCurrentMonth()
    ).flatMapLatest { (date, currency) ->
        transactionDao.getAmountAggregate(
            startDate = date.withDayOfMonth(1),
            endDate = date.with(TemporalAdjusters.lastDayOfMonth()),
            type = TransactionType.CREDIT,
            tagIds = null,
            showExcluded = false,
            selectedTxIds = null,
            currencyCode = currency.currencyCode
        )
    }
        .mapLatest { it.firstOrNull() }
        .mapLatest { it?.amount.orZero() }
        .mapLatest { it.absoluteValue }
        .distinctUntilChanged()

    override fun getSchedulesActiveThisMonth(): Flow<List<ActiveSchedule>> = currentDate
        .flatMapLatest { schedulesDao.getSchedulesActiveAtMonth(it) }
        .mapLatest { entities -> entities.map(ScheduleEntity::toActiveSchedule) }

    override fun getRecentSpends(): Flow<PagingData<TransactionEntry>> = combineTuple(
        currentDate,
        getCurrencyForCurrentMonth()
    ).flatMapLatest { (date, currency) ->
        transactionRepo.getAllTransactionsPaged(
            dateRange = date.withDayOfMonth(1) to date.with(TemporalAdjusters.lastDayOfMonth()),
            type = TransactionType.DEBIT,
            showExcluded = false,
            tagIds = null,
            folderId = null,
            currency = currency
        )
    }
}