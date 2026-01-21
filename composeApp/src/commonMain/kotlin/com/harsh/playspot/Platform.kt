package com.harsh.playspot

import androidx.compose.runtime.Composable

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

/**
 * Platform detection utilities
 */
expect val isIOS: Boolean
expect val isAndroid: Boolean

@Composable
expect fun SetStatusBarAppearance(isDarkTheme: Boolean)