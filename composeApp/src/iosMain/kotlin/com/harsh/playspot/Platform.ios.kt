package com.harsh.playspot

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import platform.Foundation.NSDate
import platform.Foundation.NSUUID
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIActivityViewController
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
    return NSUUID().UUIDString()
}

actual fun shareText(text: String, title: String) {
    val activityItems = listOf(text)
    val activityViewController = UIActivityViewController(
        activityItems = activityItems,
        applicationActivities = null
    )
    
    val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
    rootViewController?.presentViewController(
        activityViewController,
        animated = true,
        completion = null
    )
}

actual fun openMap(latitude: Double, longitude: Double, label: String) {
    // Try Apple Maps first, then fallback to Google Maps web URL
    val encodedLabel = label.replace(" ", "+")
    val appleMapsUrl = if (label.isNotBlank()) {
        "http://maps.apple.com/?ll=$latitude,$longitude&q=$encodedLabel"
    } else {
        "http://maps.apple.com/?ll=$latitude,$longitude"
    }
    
    val url = platform.Foundation.NSURL.URLWithString(appleMapsUrl)
    if (url != null) {
        UIApplication.sharedApplication.openURL(url)
    }
}

actual fun requestLocationPermission(onResult: (Boolean) -> Unit) {
    // On iOS, location permission is requested automatically when getCurrentLocation is called
    // The LocationProvider handles this through CLLocationManager
    // For now, we'll just invoke the callback - the actual permission request
    // happens in LocationProvider.ios.kt
    onResult(true)
}

actual fun openNotificationSettings() {
}