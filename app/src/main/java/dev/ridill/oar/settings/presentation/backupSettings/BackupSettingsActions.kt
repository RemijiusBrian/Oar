package dev.ridill.oar.settings.presentation.backupSettings

import dev.ridill.oar.settings.domain.modal.BackupInterval

interface BackupSettingsActions {
    fun onBackupIntervalPreferenceClick()
    fun onBackupIntervalSelected(interval: BackupInterval)
    fun onBackupIntervalSelectionDismiss()
    fun onBackupNowClick()
    fun onEncryptionPreferenceClick()
}