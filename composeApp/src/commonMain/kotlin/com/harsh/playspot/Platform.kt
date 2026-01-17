package com.harsh.playspot

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/**
 * Platform detection utilities
 */
expect val isIOS: Boolean
expect val isAndroid: Boolean