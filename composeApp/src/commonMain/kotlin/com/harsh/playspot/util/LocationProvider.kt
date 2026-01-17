package com.harsh.playspot.util

/**
 * Data class representing a geographic location
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

/**
 * Expect declaration for platform-specific location provider
 */
expect class LocationProvider() {
    /**
     * Check if location permission is granted
     */
    suspend fun hasLocationPermission(): Boolean
    
    /**
     * Get the current user location
     * Returns null if location is unavailable or permission denied
     */
    suspend fun getCurrentLocation(): UserLocation?
    
    companion object {
        /**
         * Get the singleton instance of LocationProvider
         */
        fun getInstance(): LocationProvider
    }
}
