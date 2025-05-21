package dev.ridill.oar.settings.presentation.backupSettings

import dev.ridill.oar.core.domain.util.DateUtil
import dev.ridill.oar.core.domain.util.LocaleUtil
import dev.ridill.oar.core.ui.util.UiText
import dev.ridill.oar.settings.domain.modal.BackupInterval
import dev.ridill.oar.settings.domain.repositoty.FatalBackupError
import java.time.LocalDateTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle

data class BackupSettingsState(
    val userEmail: String? = null,
    val backupInterval: BackupInterval = BackupInterval.MANUAL,
    val showBackupIntervalSelection: Boolean = false,
    val lastBackupDateTime: LocalDateTime? = null,
    val isBackupRunning: Boolean = false,
    val isEncryptionPasswordAvailable: Boolean = false,
    val fatalBackupError: FatalBackupError? = null,
) {
    val lastBackupDateFormatted: UiText?
        get() = lastBackupDateTime?.let { DateUtil.Formatters.prettyDateAgo(it.toLocalDate()) }

    fun getBackupTimeFormatted(is24HourFormat: Boolean): String? {
        var pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            null,
            FormatStyle.SHORT,
            IsoChronology.INSTANCE,
            LocaleUtil.defaultLocale
        )

        if (is24HourFormat) {
            pattern = pattern.replace("h", "H")
                .replace("a", "").trim()
        }

        val formatter = DateTimeFormatter.ofPattern(pattern)

        return lastBackupDateTime?.let { formatter.format(it) }
    }
}