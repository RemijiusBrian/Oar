package dev.ridill.oar.settings.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.UtilConstants
import dev.ridill.oar.core.domain.util.tryOrNull
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.toEntity
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.withContext
import java.util.Currency

class CurrencyRepositoryImpl(
    private val currencyListDao: CurrencyListDao
) : CurrencyRepository {

    override suspend fun initCurrenciesList(): Unit = withContext(Dispatchers.Default) {
        val entities = LocaleUtil.availableLocales
            .mapNotNull {
                tryOrNull { Currency.getInstance(it) }
            }
            .distinctBy { it.currencyCode }
            .map(Currency::toEntity)
            .toTypedArray()
        currencyListDao.upsert(*entities)
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