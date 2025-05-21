package dev.ridill.oar.settings.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.tryOrNull
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.local.CurrencyPreferenceDao
import dev.ridill.oar.settings.data.local.entity.CurrencyPreferenceEntity
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.util.Currency

class CurrencyRepositoryImpl(
    private val dao: CurrencyPreferenceDao,
    private val currencyListDao: CurrencyListDao
) : CurrencyRepository {
    override fun getCurrencyPreferenceForMonth(date: LocalDate): Flow<Currency> = dao
        .getCurrencyCodeForDateOrLast(date)
        .mapLatest { currencyCode ->
            currencyCode?.let {
                tryOrNull { LocaleUtil.currencyForCode(it) }
            } ?: LocaleUtil.defaultCurrency
        }.distinctUntilChanged()

    override suspend fun saveCurrencyPreference(currency: Currency, date: LocalDate) {
        withContext(Dispatchers.IO) {
            val entity = CurrencyPreferenceEntity(
                currencyCode = currency.currencyCode,
                date = date.withDayOfMonth(1)
            )
            dao.upsert(entity)
        }
    }

    override fun getCurrencyListPaged(
        searchQuery: String
    ): Flow<PagingData<Currency>> = Pager(
        config = PagingConfig(pageSize = UtilConstants.DEFAULT_PAGE_SIZE),
        pagingSourceFactory = { currencyListDao.getAllCurrencyCodesPaged(searchQuery) }
    ).flow
        .mapLatest { pagingData ->
            pagingData.map { Currency.getInstance(it) }
        }
}