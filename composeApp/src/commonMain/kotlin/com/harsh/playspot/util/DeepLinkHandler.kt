package com.harsh.playspot.util

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Shared deeplink handler that can be called from platform-specific code
 */
object DeepLinkHandler {
    private val _pendingDeepLink = MutableStateFlow<String?>(null)
    val pendingDeepLink: StateFlow<String?> = _pendingDeepLink.asStateFlow()
    
    fun handleDeepLink(uri: String) {
        _pendingDeepLink.value = uri
    }
    
    fun consumeDeepLink() {
        _pendingDeepLink.value = null
    }
    
    /**
     * Parse event ID from deeplink URI
     * Supports:
     * - playspot://event/{eventId}
     * - https://playspot.app/event/{eventId}
     */
    fun parseEventId(uri: String): String? {
        return when {
            // playspot://event/{eventId}
            uri.startsWith("playspot://event/") -> {
                uri.removePrefix("playspot://event/").takeIf { it.isNotBlank() }
            }
            // https://playspot.app/event/{eventId}
            uri.startsWith("https://playspot.app/event/") -> {
                uri.removePrefix("https://playspot.app/event/").split("?").firstOrNull()?.takeIf { it.isNotBlank() }
            }
            else -> null
        }
    }
    
    /**
     * Check if the deeplink is a password reset completion link
     * Supports:
     * - playspot://password-reset-complete
     * - https://playspot.app/password-reset-complete
     */
    fun isPasswordResetComplete(uri: String): Boolean {
        return uri.startsWith("playspot://password-reset-complete") ||
               uri.contains("playspot.app/password-reset-complete")
    }
}
