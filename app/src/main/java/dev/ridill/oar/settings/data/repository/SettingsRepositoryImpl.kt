package dev.ridill.oar.settings.data.repository

import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.settings.domain.modal.AppTheme
import dev.ridill.oar.settings.domain.repositoty.BudgetPreferenceRepository
import dev.ridill.oar.settings.domain.repositoty.CurrencyRepository
import dev.ridill.oar.settings.domain.repositoty.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import java.util.Currency

class SettingsRepositoryImpl(
    private val preferencesManager: PreferencesManager,
    private val budgetRepo: BudgetPreferenceRepository,
    private val currencyRepo: CurrencyRepository
) : SettingsRepository {
    private val currentDate = MutableStateFlow(DateUtil.dateNow())
    private val preferences = preferencesManager.preferences

    override fun refreshCurrentDate() {
        currentDate.update { DateUtil.dateNow() }
    }

    override fun getCurrentAppTheme(): Flow<AppTheme> = preferences
        .mapLatest { it.appTheme }
        .distinctUntilChanged()

    override suspend fun updateAppTheme(theme: AppTheme) = preferencesManager
        .updateAppThem(theme)

    override fun getDynamicColorsEnabled(): Flow<Boolean> = preferences
        .mapLatest { it.dynamicColorsEnabled }
        .distinctUntilChanged()

    override suspend fun toggleDynamicColors(enabled: Boolean) = preferencesManager
        .updateDynamicColorsEnabled(enabled)

    override fun getCurrentBudget(): Flow<Long> = currentDate.flatMapLatest {
        budgetRepo.getBudgetPreferenceForMonth(it)
    }.distinctUntilChanged()

    override suspend fun updateCurrencyPreference(currency: Currency) =
        currencyRepo.saveCurrencyPreference(currency)

    override fun getTransactionAutoDetectEnabled(): Flow<Boolean> = preferences
        .mapLatest { it.transactionAutoDetectEnabled }
        .distinctUntilChanged()

    override suspend fun toggleAutoDetectTransactions(enabled: Boolean) =
        preferencesManager.updateTransactionAutoDetectEnabled(enabled)

    override suspend fun getShowTransactionAutoDetectInfoValue(): Boolean = preferences.first()
        .showAutoDetectTxInfo

    override suspend fun toggleShowAutoDetectTxInfoFalse() =
        preferencesManager.toggleShowAutoDetectTxInfoFalse()
}