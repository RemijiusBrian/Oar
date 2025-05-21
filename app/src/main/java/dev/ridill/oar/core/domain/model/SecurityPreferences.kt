package dev.ridill.oar.core.domain.model

data class SecurityPreferences(
    val backupEncryptionHash: String?,
    val backupEncryptionHashSalt: String?
) {
    val hasValidBackupEncryptionPassword: Boolean
        get() = !backupEncryptionHash.isNullOrEmpty()
                && !backupEncryptionHashSalt.isNullOrEmpty()
}