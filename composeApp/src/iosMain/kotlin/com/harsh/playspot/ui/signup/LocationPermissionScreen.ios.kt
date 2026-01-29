package com.harsh.playspot.ui.signup

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
            scope.launch {
                // On iOS, requesting location triggers the permission dialog automatically
                locationProvider.getCurrentLocation()
                hasPermission = locationProvider.hasLocationPermission()
                onContinue()
            }
        },
        onSkip = onSkip
    )
}
