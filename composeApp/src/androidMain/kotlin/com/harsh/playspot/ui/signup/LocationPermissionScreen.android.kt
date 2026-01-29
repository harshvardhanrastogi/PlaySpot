package com.harsh.playspot.ui.signup

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import com.harsh.playspot.util.LocationProvider
import kotlinx.coroutines.launch

@Composable
actual fun LocationPermissionScreenRoute(
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var hasPermission by remember { mutableStateOf<Boolean?>(null) }
    val locationProvider = remember { LocationProvider.getInstance() }
    
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        hasPermission = fineLocationGranted || coarseLocationGranted
        onContinue()
    }
    
    // Check if permission is already granted
    LaunchedEffect(Unit) {
        hasPermission = locationProvider.hasLocationPermission()
        // If permission is already granted, continue automatically
        if (hasPermission == true) {
            onContinue()
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
