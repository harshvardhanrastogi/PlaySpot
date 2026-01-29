package com.harsh.playspot.util

/**
 * Data class representing a geographic location
 */
data class UserLocation(
    val latitude: Double,
    val longitude: Double
)

/**
 * Data class representing a geocoded address
 */
data class GeocodedAddress(
    val city: String,
    val state: String,
    val country: String
) {
    fun toDisplayString(): String {
        return when {
            city.isNotBlank() && state.isNotBlank() -> "$city, $state"
            city.isNotBlank() -> city
            state.isNotBlank() -> state
            else -> country
        }
    }
}

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
    
    /**
     * Reverse geocode coordinates to get address information
     * Returns null if geocoding fails
     */
    suspend fun reverseGeocode(latitude: Double, longitude: Double): GeocodedAddress?
    
    companion object {
        /**
         * Get the singleton instance of LocationProvider
         */
        fun getInstance(): LocationProvider
    }
}
