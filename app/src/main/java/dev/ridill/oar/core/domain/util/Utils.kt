package dev.ridill.oar.core.domain.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

inline fun <T> tryOrNull(
    tag: String = "tryOrNull()",
    tryBlock: () -> T
): T? = try {
    tryBlock()
} catch (t: Throwable) {
    t.rethrowIfCoroutineCancellation()
    logE(throwable = t, tag = tag)
    null
}

fun <T> Flow<T>.asStateFlow(
    scope: CoroutineScope,
    initialValue: T,
    sharingStarted: SharingStarted = SharingStarted.WhileSubscribed(DEFAULT_SHARING_STOP_TIMEOUT)
): StateFlow<T> = this.stateIn(scope, sharingStarted, initialValue)

private const val DEFAULT_SHARING_STOP_TIMEOUT = 5_000L