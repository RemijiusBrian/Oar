package dev.ridill.oar.settings.domain.repositoty

import androidx.paging.PagingData
import dev.ridill.oar.core.domain.util.Empty
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface CurrencyRepository {
    suspend fun initCurrenciesList()
    fun getCurrencyListPaged(searchQuery: String = String.Empty): Flow<PagingData<Currency>>
}