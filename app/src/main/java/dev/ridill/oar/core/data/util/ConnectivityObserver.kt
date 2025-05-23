package dev.ridill.oar.core.data.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.mapLatest

interface ConnectivityObserver {

    fun getConnectivityStatus(): Flow<ConnectivityStatus>

    val isConnectedFlow: Flow<Boolean>
        get() = getConnectivityStatus()
            .mapLatest { it == ConnectivityStatus.CONNECTED }
            .distinctUntilChanged()
}

enum class ConnectivityStatus {
    CONNECTED,
    LOSING,
    LOST,
    UNAVAILABLE,
    NO_INTERNET
}