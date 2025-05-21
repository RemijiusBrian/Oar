package dev.ridill.oar.settings.domain.repositoty

import androidx.paging.PagingData
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.Empty
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.Currency

interface CurrencyRepository {
    fun getCurrencyPreferenceForMonth(
        date: LocalDate = DateUtil.dateNow()
    ): Flow<Currency>

    suspend fun saveCurrencyPreference(
        currency: Currency,
        date: LocalDate = DateUtil.dateNow()
    )

    fun getCurrencyListPaged(searchQuery: String = String.Empty): Flow<PagingData<Currency>>
}