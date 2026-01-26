package com.harsh.playspot

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.harsh.playspot.util.LocationProvider

class MainActivity : ComponentActivity() {
    
    private var deepLinkUri by mutableStateOf<String?>(null)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT, // Scrim color
                android.graphics.Color.TRANSPARENT  // Detect color
            )
        )
        super.onCreate(savedInstanceState)
        configureFirebaseServices()
        // Initialize LocationProvider for location services
        LocationProvider.initialize(this)
        
        // Set activity provider for share functionality
        setActivityProvider { this }
        
        // Handle deeplink from intent
        deepLinkUri = handleDeepLink(intent)
        
        setContent {
            App(
                hasUserSession = hasUserSession(),
                deepLinkUri = deepLinkUri,
                onDeepLinkHandled = { deepLinkUri = null },
                onBackPressed = { finish() }
            )
        }
    }
    
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deeplink when app is already running
        deepLinkUri = handleDeepLink(intent)
    }
    
    private fun handleDeepLink(intent: Intent?): String? {
        return intent?.data?.toString()
    }

    private fun hasUserSession(): Boolean {
        return Firebase.auth.currentUser != null
    }

    private fun configureFirebaseServices() {
//        Firebase.auth.useEmulator("192.168.29.221", 9099)
//        Firebase.firestore.useEmulator("192.168.29.221", 8080)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(hasUserSession = false)
}