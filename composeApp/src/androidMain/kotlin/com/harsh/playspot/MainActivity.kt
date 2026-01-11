package com.harsh.playspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : ComponentActivity() {
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
        setContent {
            App(hasUserSession(), onBackPressed = {
                finish()
            })
        }
    }

    private fun hasUserSession(): Boolean {
        return Firebase.auth.currentUser != null
    }

    private fun configureFirebaseServices() {
        Firebase.auth.useEmulator("192.168.29.221", 9099)
        Firebase.firestore.useEmulator("192.168.29.221", 8080)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App(hasUserSession = false, {})
}