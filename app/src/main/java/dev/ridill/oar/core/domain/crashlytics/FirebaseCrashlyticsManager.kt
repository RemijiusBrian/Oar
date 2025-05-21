package dev.ridill.oar.core.domain.crashlytics

import com.google.firebase.Firebase
import com.google.firebase.crashlytics.crashlytics

class FirebaseCrashlyticsManager : CrashlyticsManager {
    private val crashlytics = Firebase.crashlytics

    override fun recordError(t: Throwable) {
        crashlytics.recordException(t)
    }
}