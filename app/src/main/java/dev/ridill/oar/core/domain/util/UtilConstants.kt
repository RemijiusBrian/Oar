package dev.ridill.oar.core.domain.util

import android.app.PendingIntent

object UtilConstants {
    const val DB_MONTH_AND_YEAR_FORMAT = "%m-%Y"
    const val DEBOUNCE_TIMEOUT = 250L
    const val DEFAULT_PAGE_SIZE = 10
    const val DEFAULT_TAG_LIST_LIMIT = 10
    const val FIELD_AUTO_FOCUS_DELAY = 300L

    val pendingIntentFlags: Int
        get() = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
}