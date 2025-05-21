package dev.ridill.oar.settings.domain.repositoty

import androidx.work.WorkInfo
import dev.ridill.oar.settings.domain.modal.BackupInterval
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

interface BackupSettingsRepository {
    fun getLastBackupTime(): Flow<LocalDateTime?>
    fun getImmediateBackupWorkInfo(): Flow<WorkInfo?>
    fun getPeriodicBackupWorkInfo(): Flow<WorkInfo?>
    fun getIntervalFromInfo(workInfo: WorkInfo): BackupInterval?
    suspend fun updateBackupIntervalAndScheduleJob(interval: BackupInterval)
    fun runBackupJob(interval: BackupInterval)
    fun runImmediateBackupJob()
    suspend fun restoreBackupJob()
    fun getFatalBackupError(): Flow<FatalBackupError?>
    fun isEncryptionPasswordAvailable(): Flow<Boolean>
    suspend fun isCurrentPasswordMatch(currentPasswordInput: String): Boolean
    suspend fun updateEncryptionPassword(password: String)
}