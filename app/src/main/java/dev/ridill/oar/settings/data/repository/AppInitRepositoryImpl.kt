package dev.ridill.oar.settings.data.repository

import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.domain.util.tryOrNull
import dev.ridill.oar.settings.data.local.CurrencyListDao
import dev.ridill.oar.settings.data.toEntity
import dev.ridill.oar.settings.domain.repositoty.AppInitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Currency

class AppInitRepositoryImpl(
    private val currencyListDao: CurrencyListDao
) : AppInitRepository {
    override suspend fun needsInit(): Boolean = withContext(Dispatchers.IO) {
        currencyListDao.isTableEmpty()
    }

    override suspend fun initCurrenciesList() {
        val entities = LocaleUtil.availableLocales
            .mapNotNull {
                tryOrNull { Currency.getInstance(it) }
            }
            .distinctBy { it.currencyCode }
            .map(Currency::toEntity)
            .toTypedArray()
        currencyListDao.upsert(*entities)
    }
}