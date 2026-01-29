package com.harsh.playspot.ui.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.harsh.playspot.ui.signup.LocationPermissionScreen
import com.harsh.playspot.util.LocationProvider
import kotlinx.coroutines.runBlocking

@Composable
actual fun ExploreLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit
) {
    val locationProvider = remember { LocationProvider.getInstance() }
    var permissionRequested by remember { mutableStateOf(false) }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocationGranted || coarseLocationGranted) {
            onPermissionGranted()
        } else {
            // Permission denied, but treat as skipped
            onSkip()
        }
    }
    
    // Check if already has permission
    LaunchedEffect(Unit) {
        if (locationProvider.hasLocationPermission()) {
            onPermissionGranted()
        }
    }
    
    LocationPermissionScreen(
        onEnableLocation = {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        },
        onSkip = onSkip
    )
}
