package dev.ridill.oar.settings.presentation.settings

import dev.ridill.oar.account.domain.model.AuthState
import dev.ridill.oar.core.domain.util.Zero
import dev.ridill.oar.settings.domain.modal.AppTheme

data class SettingsState(
    val authState: AuthState = AuthState.UnAuthenticated,
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val dynamicColorsEnabled: Boolean = false,
    val showAppThemeSelection: Boolean = false,
    val currentMonthlyBudget: Long = Long.Zero,
    val showAutoDetectTxOption: Boolean = false,
    val autoDetectTransactionEnabled: Boolean = false,
    val showSmsPermissionRationale: Boolean = false,
    val showAutoDetectTransactionFeatureInfo: Boolean = false,
    val sourceCodeUrl: String? = null
) {
    val hasValidSourceCodeUrl: Boolean
        get() = !sourceCodeUrl.isNullOrEmpty()
}