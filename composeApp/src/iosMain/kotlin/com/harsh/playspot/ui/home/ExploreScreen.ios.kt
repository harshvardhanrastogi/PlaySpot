package com.harsh.playspot.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.harsh.playspot.ui.signup.LocationPermissionScreen
import com.harsh.playspot.util.LocationProvider
import kotlinx.coroutines.launch

@Composable
actual fun ExploreLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val locationProvider = remember { LocationProvider.getInstance() }
    
    // Check if already has permission
    LaunchedEffect(Unit) {
        if (locationProvider.hasLocationPermission()) {
            onPermissionGranted()
        }
    }
    
    LocationPermissionScreen(
        onEnableLocation = {
            scope.launch {
                // On iOS, requesting location triggers the permission dialog automatically
                locationProvider.getCurrentLocation()
                if (locationProvider.hasLocationPermission()) {
                    onPermissionGranted()
                } else {
                    onSkip()
                }
            }
        },
        onSkip = onSkip
    )
}
