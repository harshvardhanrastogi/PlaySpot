package com.harsh.playspot.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

actual class LocationProvider {
    
    private var context: Context? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    
    actual suspend fun hasLocationPermission(): Boolean {
        val ctx = context ?: return false
        return ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(
            ctx,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    @SuppressLint("MissingPermission")
    actual suspend fun getCurrentLocation(): UserLocation? {
        val ctx = context ?: return null
        val client = fusedLocationClient ?: return null
        
        if (!hasLocationPermission()) return null
        
        // Check if location services are enabled
        val locationManager = ctx.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        
        if (!isGpsEnabled && !isNetworkEnabled) return null
        
        return suspendCancellableCoroutine { continuation ->
            val cancellationTokenSource = CancellationTokenSource()
            
            client.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    continuation.resume(
                        UserLocation(
                            latitude = location.latitude,
                            longitude = location.longitude
                        )
                    )
                } else {
                    // Try to get last known location as fallback
                    client.lastLocation.addOnSuccessListener { lastLocation: Location? ->
                        if (lastLocation != null) {
                            continuation.resume(
                                UserLocation(
                                    latitude = lastLocation.latitude,
                                    longitude = lastLocation.longitude
                                )
                            )
                        } else {
                            continuation.resume(null)
                        }
                    }.addOnFailureListener {
                        continuation.resume(null)
                    }
                }
            }.addOnFailureListener {
                continuation.resume(null)
            }
            
            continuation.invokeOnCancellation {
                cancellationTokenSource.cancel()
            }
        }
    }
    
    @Suppress("DEPRECATION")
    actual suspend fun reverseGeocode(latitude: Double, longitude: Double): GeocodedAddress? {
        val ctx = context ?: return null
        
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(ctx, Locale.getDefault())
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // Use the new async API for Android 13+
                    suspendCancellableCoroutine { continuation ->
                        geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                            val address = addresses.firstOrNull()
                            if (address != null) {
                                continuation.resume(
                                    GeocodedAddress(
                                        city = address.locality ?: address.subAdminArea ?: "",
                                        state = address.adminArea ?: "",
                                        country = address.countryName ?: ""
                                    )
                                )
                            } else {
                                continuation.resume(null)
                            }
                        }
                    }
                } else {
                    // Use the deprecated sync API for older versions
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    val address = addresses?.firstOrNull()
                    if (address != null) {
                        GeocodedAddress(
                            city = address.locality ?: address.subAdminArea ?: "",
                            state = address.adminArea ?: "",
                            country = address.countryName ?: ""
                        )
                    } else {
                        null
                    }
                }
            } catch (e: Exception) {
                null
            }
        }
    }
    
    actual companion object {
        private var instance: LocationProvider? = null
        
        fun initialize(context: Context) {
            if (instance == null) {
                instance = LocationProvider().apply {
                    this.context = context.applicationContext
                    this.fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                }
            }
        }
        
        actual fun getInstance(): LocationProvider {
            return instance ?: LocationProvider()
        }
    }
}
