package com.harsh.playspot

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual val isIOS: Boolean = false
actual val isAndroid: Boolean = true

@Composable
actual fun SetStatusBarAppearance(isDarkTheme: Boolean) {
    val view = LocalView.current
    val context = LocalContext.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (context as Activity).window
            // isAppearanceLightStatusBars = true makes icons DARK
            // isAppearanceLightStatusBars = false makes icons WHITE
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !isDarkTheme
        }
    }
}

actual fun currentTimeMillis(): Long {
    return System.currentTimeMillis()
}

actual fun generateUniqueId(): String {
    return java.util.UUID.randomUUID().toString()
}

private var activityProvider: (() -> Activity?)? = null

fun setActivityProvider(provider: () -> Activity?) {
    activityProvider = provider
}

actual fun shareText(text: String, title: String) {
    val activity = activityProvider?.invoke() ?: return
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, text)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, title.ifBlank { null })
    activity.startActivity(shareIntent)
}

actual fun openMap(latitude: Double, longitude: Double, label: String) {
    val activity = activityProvider?.invoke() ?: return
    // Use geo URI which works with Google Maps, Waze, and other map apps
    val encodedLabel = java.net.URLEncoder.encode(label, "UTF-8")
    val geoUri = if (label.isNotBlank()) {
        "geo:$latitude,$longitude?q=$latitude,$longitude($encodedLabel)"
    } else {
        "geo:$latitude,$longitude?q=$latitude,$longitude"
    }
    val intent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(geoUri))
    try {
        activity.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to Google Maps web URL if no map app is installed
        val webUrl = "https://www.google.com/maps/search/?api=1&query=$latitude,$longitude"
        val webIntent = Intent(Intent.ACTION_VIEW, android.net.Uri.parse(webUrl))
        activity.startActivity(webIntent)
    }
}

actual fun requestLocationPermission(onResult: (Boolean) -> Unit) {
    val activity = activityProvider?.invoke()
    if (activity == null) {
        onResult(false)
        return
    }
    
    // Check if permission is already granted
    val hasPermission = ContextCompat.checkSelfPermission(
        activity,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    
    if (hasPermission) {
        onResult(true)
    } else {
        // For runtime permission request, use the Compose permission launcher
        // This function is kept for non-Compose contexts
        onResult(false)
    }
}