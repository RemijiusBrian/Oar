package dev.ridill.oar.settings.presentation.backupSettings

import android.app.PendingIntent
import android.content.Intent
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import com.zhuinden.flowcombinetuplekt.combineTuple
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.ridill.oar.R
import dev.ridill.oar.account.domain.model.AuthState
import dev.ridill.oar.account.domain.repository.AuthRepository
import dev.ridill.oar.account.presentation.util.AuthorizationService
import dev.ridill.oar.core.domain.model.Result
import dev.ridill.oar.core.domain.util.EventBus
import dev.ridill.oar.core.domain.util.asStateFlow
import dev.ridill.oar.core.domain.util.logD
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.settings.domain.backup.BackupWorkManager
import dev.ridill.oar.settings.domain.modal.BackupInterval
import dev.ridill.oar.settings.domain.repositoty.BackupSettingsRepository
import dev.ridill.oar.settings.presentation.backupEncryption.ENCRYPTION_PASSWORD_UPDATED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BackupSettingsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val backupSettingsRepo: BackupSettingsRepository,
    private val authRepo: AuthRepository,
    private val eventBus: EventBus<BackupSettingsEvent>
) : ViewModel(), BackupSettingsActions {

    private val userEmail = authRepo.getAuthState()
        .mapLatest {
            if (it is AuthState.Authenticated) it.account.email
            else null
        }
        .distinctUntilChanged()

    private val backupInterval = MutableStateFlow(BackupInterval.MANUAL)

    private val showBackupIntervalSelection = savedStateHandle
        .getStateFlow(SHOW_BACKUP_INTERVAL_SELECTION, false)

    private val lastBackupDateTime = backupSettingsRepo.getLastBackupTime()
        .distinctUntilChanged()

    private val isBackupRunning = MutableStateFlow(false)

    private val isEncryptionPasswordAvailable = backupSettingsRepo.isEncryptionPasswordAvailable()

    private val fatalBackupError = backupSettingsRepo.getFatalBackupError()

    val state = combineTuple(
        userEmail,
        backupInterval,
        showBackupIntervalSelection,
        lastBackupDateTime,
        isBackupRunning,
        isEncryptionPasswordAvailable,
        fatalBackupError,
    ).mapLatest { (
                      userEmail,
                      backupInterval,
                      showBackupIntervalSelection,
                      lastBackupDateTime,
                      isBackupRunning,
                      isEncryptionPasswordAvailable,
                      fatalBackupError,

                  ) ->
        BackupSettingsState(
            userEmail = userEmail,
            backupInterval = backupInterval,
            showBackupIntervalSelection = showBackupIntervalSelection,
            lastBackupDateTime = lastBackupDateTime,
            isBackupRunning = isBackupRunning,
            isEncryptionPasswordAvailable = isEncryptionPasswordAvailable,
            fatalBackupError = fatalBackupError,
        )
    }.asStateFlow(viewModelScope, BackupSettingsState())

    val events = eventBus.eventFlow

    init {
        collectImmediateBackupWorkInfo()
        collectPeriodicBackupWorkInfo()
    }

    private var hasBackupJobRunThisSession: Boolean = false
    private fun collectImmediateBackupWorkInfo() = viewModelScope.launch {
        backupSettingsRepo.getImmediateBackupWorkInfo().collectLatest { info ->
            logD { "Immediate Backup Work Info - $info" }
            val isRunning = info?.state == WorkInfo.State.RUNNING
            isBackupRunning.update { isRunning }
            if (isRunning) {
                hasBackupJobRunThisSession = true
            }

            // if check to prevent showing message without running backup job at least once.
            // hasBackupJobRunThisSession boolean is set to true when backup job is running.
            // Without this check WorkInfo output message will be shown everytime user arrives at screen
            // Even if backup was run long back
            if (hasBackupJobRunThisSession) {
                info?.outputData?.getString(BackupWorkManager.KEY_MESSAGE)?.let {
                    eventBus.send(BackupSettingsEvent.ShowUiMessage(UiText.DynamicString(it)))
                }
            }
        }
    }

    private fun collectPeriodicBackupWorkInfo() = viewModelScope.launch {
        backupSettingsRepo.getPeriodicBackupWorkInfo().collectLatest { info ->
            updateBackupInterval(info)
            logD { "Periodic Backup Work Info - $info" }
            isBackupRunning.update {
                info?.state == WorkInfo.State.RUNNING
            }
        }
    }

    private fun updateBackupInterval(info: WorkInfo?) = viewModelScope.launch {
        val interval = if (info?.state == WorkInfo.State.CANCELLED)
            BackupInterval.MANUAL
        else info?.let(backupSettingsRepo::getIntervalFromInfo)
        backupInterval.update { interval ?: BackupInterval.MANUAL }
    }

    override fun onBackupIntervalPreferenceClick() {
        viewModelScope.launch {
            val isEncryptionPasswordAvailable = isEncryptionPasswordAvailable.first()
            if (!isEncryptionPasswordAvailable) {
                eventBus.send(BackupSettingsEvent.NavigateToBackupEncryptionScreen)
                return@launch
            }
            savedStateHandle[SHOW_BACKUP_INTERVAL_SELECTION] = true
        }
    }

    override fun onBackupIntervalSelected(interval: BackupInterval) {
        viewModelScope.launch {
            savedStateHandle[SHOW_BACKUP_INTERVAL_SELECTION] = false
            backupSettingsRepo.updateBackupIntervalAndScheduleJob(interval)
        }
    }

    override fun onBackupIntervalSelectionDismiss() {
        savedStateHandle[SHOW_BACKUP_INTERVAL_SELECTION] = false
    }

    override fun onBackupNowClick() {
        viewModelScope.launch {
            val isEncryptionPasswordAvailable = isEncryptionPasswordAvailable.first()
            if (!isEncryptionPasswordAvailable) {
                eventBus.send(BackupSettingsEvent.NavigateToBackupEncryptionScreen)
                return@launch
            }
            when (val result = authRepo.authorizeUserAccount()) {
                is Result.Error -> {
                    when (result.error) {
                        AuthorizationService.AuthorizationError.NEEDS_RESOLUTION -> {
                            result.data?.let {
                                eventBus.send(BackupSettingsEvent.StartAuthorizationFlow(it))
                            }
                        }

                        AuthorizationService.AuthorizationError.AUTHORIZATION_FAILED -> {
                            eventBus.send(BackupSettingsEvent.ShowUiMessage(result.message))
                        }
                    }
                }

                is Result.Success -> {
                    backupSettingsRepo.runImmediateBackupJob()
                }
            }
        }
    }

    fun onAuthorizationResult(intent: Intent) = viewModelScope.launch {
        when (val result = authRepo.decodeAuthorizationResult(intent)) {
            is Result.Error -> {
                eventBus.send(BackupSettingsEvent.ShowUiMessage(result.message))
            }

            is Result.Success -> Unit
        }
    }

    override fun onEncryptionPreferenceClick() {
        viewModelScope.launch {
            eventBus.send(BackupSettingsEvent.NavigateToBackupEncryptionScreen)
        }
    }

    fun onDestinationResult(result: String) {
        viewModelScope.launch {
            when (result) {
                ENCRYPTION_PASSWORD_UPDATED -> {
                    eventBus.send(BackupSettingsEvent.ShowUiMessage(UiText.StringResource(R.string.encryption_password_updated)))
                    val interval = backupInterval.first()
                    backupSettingsRepo.runBackupJob(interval)
                }

                else -> Unit
            }
        }
    }

    sealed interface BackupSettingsEvent {
        data class ShowUiMessage(val uiText: UiText) : BackupSettingsEvent
        data object NavigateToBackupEncryptionScreen : BackupSettingsEvent
        data class StartAuthorizationFlow(val pendingIntent: PendingIntent) : BackupSettingsEvent
    }
}

private const val SHOW_BACKUP_INTERVAL_SELECTION = "SHOW_BACKUP_INTERVAL_SELECTION"