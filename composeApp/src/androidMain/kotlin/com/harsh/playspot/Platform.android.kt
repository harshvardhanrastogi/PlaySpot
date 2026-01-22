package com.harsh.playspot

import android.app.Activity
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import kotlinx.io.files.SystemFileSystem

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