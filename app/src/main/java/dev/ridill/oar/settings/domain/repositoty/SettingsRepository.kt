package dev.ridill.oar.settings.domain.repositoty

import dev.ridill.oar.settings.domain.modal.AppTheme
import kotlinx.coroutines.flow.Flow
import java.util.Currency

interface SettingsRepository {
    fun refreshCurrentDate()
    fun getCurrentAppTheme(): Flow<AppTheme>
    suspend fun updateAppTheme(theme: AppTheme)
    fun getDynamicColorsEnabled(): Flow<Boolean>
    suspend fun toggleDynamicColors(enabled: Boolean)
    fun getTransactionAutoDetectEnabled(): Flow<Boolean>
    suspend fun toggleAutoDetectTransactions(enabled: Boolean)
    suspend fun getShowTransactionAutoDetectInfoValue(): Boolean
    suspend fun toggleShowAutoDetectTxInfoFalse()
}