package dev.ridill.oar.settings.presentation.securitySettings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.core.data.preferences.PreferencesManager
import dev.ridill.oar.core.domain.util.BuildUtil
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.asStateFlow
import dev.ridill.oar.settings.domain.appLock.AppAutoLockInterval
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SecuritySettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val preferencesManager: PreferencesManager,
    private val eventBus: EventBus<SecuritySettingsEvent>
) : ViewModel(), SecuritySettingsActions {

    private val preferences = preferencesManager.preferences
    private val appLockEnabled = preferences
        .mapLatest { it.appLockEnabled }
        .distinctUntilChanged()

    private val autoLockInterval = preferences
        .mapLatest { it.appAutoLockInterval }
        .distinctUntilChanged()

    private val screenSecurityEnabled = preferences
        .mapLatest { it.screenSecurityEnabled }
        .distinctUntilChanged()

    private val showNotificationPermissionRationale = savedStateHandle
        .getStateFlow(SHOW_NOTIFICATION_PERMISSION_RATIONALE, false)

    val state = combineTuple(
        appLockEnabled,
        autoLockInterval,
        screenSecurityEnabled,
        showNotificationPermissionRationale
    ).mapLatest { (
                      appLockEnabled,
                      autoLockInterval,
                      screenSecurityEnabled,
                      showNotificationPermissionRationale
                  ) ->
        SecuritySettingsState(
            appLockEnabled = appLockEnabled,
            autoLockInterval = autoLockInterval,
            screenSecurityEnabled = screenSecurityEnabled,
            showNotificationPermissionRationale = showNotificationPermissionRationale
        )
    }.asStateFlow(viewModelScope, SecuritySettingsState())

    val events = eventBus.eventFlow

    override fun onAppLockToggle(enabled: Boolean) {
        viewModelScope.launch {
            if (!enabled) {
                preferencesManager.updateAppLockEnabled(false)
                return@launch
            }
            if (BuildUtil.isNotificationRuntimePermissionNeeded()) {
                eventBus.send(SecuritySettingsEvent.CheckNotificationPermission)
            } else {
                eventBus.send(SecuritySettingsEvent.LaunchBiometricAuthentication)
            }
        }
    }

    fun onNotificationPermissionResult(granted: Boolean) = viewModelScope.launch {
        if (granted) {
            eventBus.send(SecuritySettingsEvent.LaunchBiometricAuthentication)
        } else {
            savedStateHandle[SHOW_NOTIFICATION_PERMISSION_RATIONALE] = true
        }
    }

    fun onAuthenticationSuccess() {
        viewModelScope.launch {
            preferencesManager.updateAppLockEnabled(true)
        }
    }

    override fun onAutoLockIntervalSelect(interval: AppAutoLockInterval) {
        viewModelScope.launch {
            preferencesManager.updateAppAutoLockInterval(interval)
        }
    }

    override fun onScreenSecurityToggle(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.updateScreenSecurityEnabled(enabled)
        }
    }

    override fun onNotificationPermissionRationaleDismiss() {
        savedStateHandle[SHOW_NOTIFICATION_PERMISSION_RATIONALE] = false
    }

    override fun onNotificationPermissionRationaleConfirm() {
        viewModelScope.launch {
            savedStateHandle[SHOW_NOTIFICATION_PERMISSION_RATIONALE] = false
            eventBus.send(SecuritySettingsEvent.NavigateToNotificationSettings)
        }
    }

    sealed interface SecuritySettingsEvent {
        data object CheckNotificationPermission : SecuritySettingsEvent
        data object LaunchBiometricAuthentication : SecuritySettingsEvent
        data object NavigateToNotificationSettings : SecuritySettingsEvent
    }
}

private const val SHOW_NOTIFICATION_PERMISSION_RATIONALE = "SHOW_NOTIFICATION_PERMISSION_RATIONALE"