package dev.ridill.oar.settings.presentation.backupEncryption

data class BackupEncryptionState(
    val hasExistingPassword: Boolean = false,
    val showPasswordInput: Boolean = false,
    val isLoading: Boolean = false,
    val isPasswordUpdateButtonLoading: Boolean = false
)