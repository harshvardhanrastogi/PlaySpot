package com.harsh.playspot.data.places

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class PlacesAutocompleteResponse(
    val predictions: List<PlacePrediction> = emptyList(),
    val status: String = ""
)

@Serializable
data class PlacePrediction(
    val description: String = "",
    @SerialName("place_id")
    val placeId: String = "",
    @SerialName("structured_formatting")
    val structuredFormatting: StructuredFormatting? = null
)

@Serializable
data class StructuredFormatting(
    @SerialName("main_text")
    val mainText: String = "",
    @SerialName("secondary_text")
    val secondaryText: String = ""
)

@Serializable
data class PlaceDetailsResponse(
    val result: PlaceResult? = null,
    val status: String = ""
)

@Serializable
data class PlaceResult(
    val name: String = "",
    @SerialName("formatted_address")
    val formattedAddress: String = "",
    val geometry: Geometry? = null
)

@Serializable
data class Geometry(
    val location: LatLng? = null
)

@Serializable
data class LatLng(
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

@Serializable
data class NearbySearchResponse(
    val results: List<NearbyPlace> = emptyList(),
    val status: String = ""
)

@Serializable
data class NearbyPlace(
    val name: String = "",
    @SerialName("place_id")
    val placeId: String = "",
    val vicinity: String? = null,
    val geometry: Geometry? = null
)

data class PlaceSearchResult(
    val placeId: String,
    val name: String,
    val address: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val distanceMeters: Double? = null
) {
    /**
     * Get formatted distance string
     */
    fun getFormattedDistance(): String? {
        val distance = distanceMeters ?: return null
        return when {
            distance < 1000 -> "${distance.toInt()} m"
            else -> String.format("%.1f km", distance / 1000)
        }
    }
}

class PlacesRepository(
    private val apiKey: String
) {
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    private val baseUrl = "https://maps.googleapis.com/maps/api/place"
    
    /**
     * Calculate distance between two points using Haversine formula
     * @return distance in meters
     */
    private fun calculateDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double
    ): Double {
        val earthRadius = 6371000.0 // Earth's radius in meters
        
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLon / 2) * kotlin.math.sin(dLon / 2)
        
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        
        return earthRadius * c
    }
    
    // Common sport names that should be searched with venue suffixes
    private val sportNames = setOf(
        "badminton", "tennis", "cricket", "football", "soccer", "basketball",
        "volleyball", "squash", "table tennis", "ping pong", "golf", "swimming",
        "hockey", "rugby", "baseball", "softball", "pickleball", "padel",
        "bowling", "archery", "boxing", "martial arts", "karate", "judo",
        "taekwondo", "wrestling", "fencing", "skating", "ice skating",
        "roller skating", "cycling", "running", "jogging", "yoga", "pilates",
        "crossfit", "weightlifting", "gym", "fitness", "athletics", "track"
    )
    
    // Venue suffixes for sport-specific searches
    private val venueSuffixes = listOf("court", "club", "academy", "ground", "arena", "center", "stadium")

    /**
     * Search for places with priority on sports establishments
     * Restricted to a specific location/city
     * 
     * @param query Search query
     * @param latitude Center latitude for location bias (also used for distance calculation)
     * @param longitude Center longitude for location bias (also used for distance calculation)
     * @param radiusMeters Search radius in meters (default 10km for city-level search)
     * @param prioritizeSports Whether to prioritize sports-related venues
     */
    suspend fun searchPlaces(
        query: String,
        latitude: Double? = null,
        longitude: Double? = null,
        radiusMeters: Int = 10000, // 10km radius for city-level search
        prioritizeSports: Boolean = true
    ): Result<List<PlaceSearchResult>> {
        if (query.isBlank()) return Result.success(emptyList())

        return try {
            // Build optimized query for sports venues
            val optimizedQuery = buildSportsQuery(query, prioritizeSports)

            val response: PlacesAutocompleteResponse = client.get("$baseUrl/autocomplete/json") {
                parameter("input", optimizedQuery)
                parameter("key", apiKey)
                
                // Location bias - prefer results near user's location (soft bias, not strict)
                if (latitude != null && longitude != null) {
                    parameter("location", "$latitude,$longitude")
                    parameter("radius", radiusMeters)
                    // Don't use strictbounds - allow results outside radius but prefer nearby
                }
            }.body()

            if (response.status == "OK" || response.status == "ZERO_RESULTS") {
                // Fetch details for each place concurrently to get coordinates and calculate distance
                val resultsWithDistance = coroutineScope {
                    response.predictions.map { prediction ->
                        async {
                            fetchPlaceWithDistance(
                                placeId = prediction.placeId,
                                name = prediction.structuredFormatting?.mainText ?: prediction.description,
                                address = prediction.structuredFormatting?.secondaryText ?: "",
                                userLat = latitude,
                                userLng = longitude
                            )
                        }
                    }.awaitAll()
                }
                
                // Sort by distance (nearest first), places without distance go to end
                val sortedResults = resultsWithDistance.sortedWith(
                    compareBy(nullsLast()) { it.distanceMeters }
                )
                
                Result.success(sortedResults)
            } else {
                Result.failure(Exception("Places API error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Fetch place details and calculate distance
     */
    private suspend fun fetchPlaceWithDistance(
        placeId: String,
        name: String,
        address: String,
        userLat: Double?,
        userLng: Double?
    ): PlaceSearchResult {
        return try {
            val response: PlaceDetailsResponse = client.get("$baseUrl/details/json") {
                parameter("place_id", placeId)
                parameter("fields", "geometry")
                parameter("key", apiKey)
            }.body()
            
            val placeLat = response.result?.geometry?.location?.lat
            val placeLng = response.result?.geometry?.location?.lng
            
            val distance = if (userLat != null && userLng != null && 
                               placeLat != null && placeLng != null) {
                calculateDistance(userLat, userLng, placeLat, placeLng)
            } else null
            
            PlaceSearchResult(
                placeId = placeId,
                name = name,
                address = address,
                latitude = placeLat,
                longitude = placeLng,
                distanceMeters = distance
            )
        } catch (e: Exception) {
            // If details fetch fails, return without coordinates
            PlaceSearchResult(
                placeId = placeId,
                name = name,
                address = address
            )
        }
    }
    
    /**
     * Build an optimized search query for sports venues
     * Uses query directly - Places API autocomplete works best with natural queries
     */
    private fun buildSportsQuery(query: String, prioritizeSports: Boolean): String {
        // Use query as-is - Places API autocomplete is smart enough
        // to find relevant venues for sport names like "badminton", "tennis", etc.
        return query.trim()
    }
    
    /**
     * Check if a query is a known sport name
     */
    fun isSportQuery(query: String): Boolean {
        val lowerQuery = query.lowercase().trim()
        return sportNames.any { sport ->
            lowerQuery == sport || 
            lowerQuery.startsWith("$sport ") || 
            lowerQuery.endsWith(" $sport") ||
            lowerQuery.contains(sport)
        }
    }

    /**
     * Search nearby sports venues using Places Nearby Search
     */
    suspend fun searchNearbySportsVenues(
        latitude: Double,
        longitude: Double,
        radiusMeters: Int = 5000
    ): Result<List<PlaceSearchResult>> {
        return try {
            val response: NearbySearchResponse = client.get("$baseUrl/nearbysearch/json") {
                parameter("location", "$latitude,$longitude")
                parameter("radius", radiusMeters)
                parameter("type", "gym|stadium|park")
                parameter("keyword", "sports")
                parameter("key", apiKey)
            }.body()

            if (response.status == "OK" || response.status == "ZERO_RESULTS") {
                val results = response.results.map { place ->
                    PlaceSearchResult(
                        placeId = place.placeId,
                        name = place.name,
                        address = place.vicinity ?: "",
                        latitude = place.geometry?.location?.lat,
                        longitude = place.geometry?.location?.lng
                    )
                }
                Result.success(results)
            } else {
                Result.failure(Exception("Nearby search error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get place details with optional distance calculation
     * @param placeId The place ID to fetch details for
     * @param userLat User's current latitude (optional, for distance calculation)
     * @param userLng User's current longitude (optional, for distance calculation)
     */
    suspend fun getPlaceDetails(
        placeId: String,
        userLat: Double? = null,
        userLng: Double? = null
    ): Result<PlaceSearchResult> {
        return try {
            val response: PlaceDetailsResponse = client.get("$baseUrl/details/json") {
                parameter("place_id", placeId)
                parameter("fields", "name,formatted_address,geometry")
                parameter("key", apiKey)
            }.body()

            if (response.status == "OK" && response.result != null) {
                val result = response.result
                val placeLat = result.geometry?.location?.lat
                val placeLng = result.geometry?.location?.lng
                
                // Calculate distance if we have both user and place coordinates
                val distance = if (userLat != null && userLng != null && 
                                   placeLat != null && placeLng != null) {
                    calculateDistance(userLat, userLng, placeLat, placeLng)
                } else null
                
                Result.success(
                    PlaceSearchResult(
                        placeId = placeId,
                        name = result.name,
                        address = result.formattedAddress,
                        latitude = placeLat,
                        longitude = placeLng,
                        distanceMeters = distance
                    )
                )
            } else {
                Result.failure(Exception("Place details error: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    /**
     * Add distance to a PlaceSearchResult
     */
    fun withDistance(
        place: PlaceSearchResult,
        userLat: Double,
        userLng: Double
    ): PlaceSearchResult {
        val placeLat = place.latitude ?: return place
        val placeLng = place.longitude ?: return place
        
        val distance = calculateDistance(userLat, userLng, placeLat, placeLng)
        return place.copy(distanceMeters = distance)
    }

    companion object {
        @Volatile
        private var instance: PlacesRepository? = null

        fun getInstance(apiKey: String): PlacesRepository {
            return instance ?: synchronized(this) {
                instance ?: PlacesRepository(apiKey).also { instance = it }
            }
        }
    }
}
