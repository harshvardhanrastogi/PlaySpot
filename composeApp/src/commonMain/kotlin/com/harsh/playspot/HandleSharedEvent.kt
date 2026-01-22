package com.harsh.playspot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.harsh.playspot.ui.events.EventManager

@Composable
fun HandleSharedEvents(eventManager: EventManager) {
    LaunchedEffect(true) {
        SharedEventHandler.events.collect { event ->
            when (event) {
                SharedEvent.EventCreated -> {
                    eventManager.refreshEvents()
                }
            }
        }
    }
}