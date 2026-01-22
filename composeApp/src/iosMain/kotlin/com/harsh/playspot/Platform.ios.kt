package com.harsh.playspot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.Foundation.NSDate
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.UIDevice
import platform.UIKit.UIStatusBarStyleDarkContent
import platform.UIKit.UIStatusBarStyleLightContent
import platform.UIKit.setStatusBarStyle

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()

@Composable
actual fun SetStatusBarAppearance(isDarkTheme: Boolean) {
    SideEffect {
        val style = if (!isDarkTheme) UIStatusBarStyleDarkContent else UIStatusBarStyleLightContent
        UIApplication.sharedApplication.setStatusBarStyle(style, animated = true)
    }
}

actual val isIOS: Boolean = true
actual val isAndroid: Boolean = false
actual fun currentTimeMillis(): Long {
    return (NSDate().timeIntervalSince1970 * 1000).toLong()
}

actual fun generateUniqueId(): String {
    return platform.Foundation.NSUUID().UUIDString()
}