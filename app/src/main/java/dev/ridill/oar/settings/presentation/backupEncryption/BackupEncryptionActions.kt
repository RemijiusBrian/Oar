package dev.ridill.oar.settings.presentation.backupEncryption

interface BackupEncryptionActions {
    fun onUpdatePasswordClick()
    fun onForgotCurrentPasswordClick()
    fun onPasswordInputDismiss()
    fun onPasswordUpdateConfirm()
}