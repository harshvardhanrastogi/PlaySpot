package com.harsh.playspot.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

data class MapLocation(
    val latitude: Double,
    val longitude: Double,
    val name: String = "",
    val address: String = ""
)

/**
 * Cross-platform Google Map composable
 * - Android: Uses Google Maps SDK
 * - iOS: Uses placeholder (native maps have UIKitView compatibility issues)
 */
@Composable
expect fun GoogleMapView(
    modifier: Modifier = Modifier,
    initialLocation: MapLocation = MapLocation(37.7749, -122.4194), // San Francisco default
    markers: List<MapLocation> = emptyList(),
    onMapClick: (MapLocation) -> Unit = {},
    onMarkerClick: (MapLocation) -> Unit = {}
)
