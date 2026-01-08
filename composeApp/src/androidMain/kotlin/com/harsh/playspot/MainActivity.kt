package com.harsh.playspot

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen

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
            App(onBackPressed = {
                finish()
            })
        }
    }

    private fun configureFirebaseServices() {

    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App({})
}