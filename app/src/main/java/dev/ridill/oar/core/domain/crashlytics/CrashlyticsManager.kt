package dev.ridill.oar.core.domain.crashlytics

interface CrashlyticsManager {
    fun recordError(t: Throwable)
}