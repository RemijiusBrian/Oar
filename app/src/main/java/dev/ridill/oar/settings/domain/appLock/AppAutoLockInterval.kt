package dev.ridill.oar.settings.domain.appLock

import androidx.annotation.StringRes
import dev.ridill.oar.R
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

enum class AppAutoLockInterval(
    val duration: Duration,
    @StringRes val labelRes: Int,
    val debugOnly: Boolean = false
) {
    FIVE_SECONDS(duration = 5.seconds, labelRes = R.string.five_seconds, debugOnly = true),
    ONE_MINUTE(duration = 1.minutes, labelRes = R.string.one_minute),
    FIVE_MINUTE(duration = 5.minutes, labelRes = R.string.five_minutes),
    TEN_MINUTE(duration = 10.minutes, labelRes = R.string.ten_minutes)
}