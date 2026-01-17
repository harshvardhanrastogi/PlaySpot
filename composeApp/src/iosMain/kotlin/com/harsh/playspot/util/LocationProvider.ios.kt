package com.harsh.playspot.util

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.CoreLocation.CLAuthorizationStatus
import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedAlways
import platform.CoreLocation.kCLAuthorizationStatusAuthorizedWhenInUse
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.Foundation.NSError
import platform.darwin.NSObject
import kotlin.coroutines.resume

actual class LocationProvider {
    
    private val locationManager = CLLocationManager()
    
    actual companion object {
        private val instance = LocationProvider()
        
        actual fun getInstance(): LocationProvider = instance
    }
    
    actual suspend fun hasLocationPermission(): Boolean {
        val status = CLLocationManager.authorizationStatus()
        return status == kCLAuthorizationStatusAuthorizedWhenInUse ||
               status == kCLAuthorizationStatusAuthorizedAlways
    }
    
    @OptIn(ExperimentalForeignApi::class)
    actual suspend fun getCurrentLocation(): UserLocation? {
        if (!hasLocationPermission()) {
            // Request permission if not granted
            locationManager.requestWhenInUseAuthorization()
            return null
        }
        
        return suspendCancellableCoroutine { continuation ->
            val delegate = object : NSObject(), CLLocationManagerDelegateProtocol {
                override fun locationManager(
                    manager: CLLocationManager,
                    didUpdateLocations: List<*>
                ) {
                    val location = didUpdateLocations.lastOrNull() as? CLLocation
                    if (location != null) {
                        location.coordinate.useContents {
                            continuation.resume(
                                UserLocation(
                                    latitude = latitude,
                                    longitude = longitude
                                )
                            )
                        }
                    } else {
                        continuation.resume(null)
                    }
                    manager.stopUpdatingLocation()
                }
                
                override fun locationManager(
                    manager: CLLocationManager,
                    didFailWithError: NSError
                ) {
                    continuation.resume(null)
                    manager.stopUpdatingLocation()
                }
            }
            
            locationManager.delegate = delegate
            locationManager.desiredAccuracy = kCLLocationAccuracyBest
            locationManager.startUpdatingLocation()
            
            continuation.invokeOnCancellation {
                locationManager.stopUpdatingLocation()
            }
        }
    }
}
