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

expect fun currentTimeMillis(): Long

/**
 * Generate a unique ID (UUID-like string)
 */
expect fun generateUniqueId(): String

/**
 * Share text content using the platform's native share sheet
 * @param text The text content to share
 * @param title Optional title for the share sheet
 */
expect fun shareText(text: String, title: String = "")

/**
 * Open a location in the device's default map application
 * @param latitude The latitude of the location
 * @param longitude The longitude of the location
 * @param label Optional label for the location marker
 */
expect fun openMap(latitude: Double, longitude: Double, label: String = "")

/**
 * Request location permission from the user
 * @param onResult Callback with true if permission granted, false otherwise
 */
expect fun requestLocationPermission(onResult: (Boolean) -> Unit)