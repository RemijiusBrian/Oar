package dev.ridill.oar.core.domain.util

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class EventBus<T> {

    private val eventsChannel = Channel<T>()
    val eventFlow get() = eventsChannel.receiveAsFlow()

    suspend fun send(event: T) {
        eventsChannel.send(event)
    }
}