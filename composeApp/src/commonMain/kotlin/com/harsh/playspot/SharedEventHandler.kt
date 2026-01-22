package com.harsh.playspot

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow


object SharedEventHandler {
    // Use Channel for guaranteed delivery - events are buffered until consumed
    private val _events = Channel<SharedEvent>(capacity = Channel.BUFFERED)
    val events: Flow<SharedEvent> = _events.receiveAsFlow()

    fun sendEvent(event: SharedEvent) {
        _events.trySend(event)
    }
}

sealed interface SharedEvent {
    data object EventCreated : SharedEvent
}