package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.BuildConfig
import com.harsh.playspot.data.places.PlaceSearchResult
import com.harsh.playspot.data.places.PlacesRepository
import com.harsh.playspot.util.LocationProvider
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LocationSelectionUiState(
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val searchResults: List<PlaceSearchResult> = emptyList(),
    val errorMessage: String? = null,
    val selectedLocation: PlaceSearchResult? = null,
    val mapCenterLat: Double = 28.6139, // Default to Delhi
    val mapCenterLng: Double = 77.2090,
    // User's current city location for search restriction
    val userCityLat: Double? = null,
    val userCityLng: Double? = null,
    val searchRadiusMeters: Int = 8000, // 8km strict radius limit
    // Current location fetching state
    val isFetchingLocation: Boolean = false,
    val hasLocationPermission: Boolean = false
)

@OptIn(FlowPreview::class)
class LocationSelectionViewModel(
    private val placesRepository: PlacesRepository = PlacesRepository.getInstance(
        apiKey = BuildConfig.GOOGLE_PLACES_API_KEY
    ),
    private val locationProvider: LocationProvider = LocationProvider.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LocationSelectionUiState())
    val uiState: StateFlow<LocationSelectionUiState> = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")

    init {
        // Debounce search to avoid too many API calls
        viewModelScope.launch {
            searchQueryFlow
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    if (query.isNotBlank()) {
                        performSearch(query)
                    } else {
                        _uiState.update { it.copy(searchResults = emptyList(), isLoading = false) }
                    }
                }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchQueryFlow.value = query

        if (query.isNotBlank()) {
            _uiState.update { it.copy(isLoading = true) }
        }
    }

    private suspend fun performSearch(query: String) {
        val state = _uiState.value
        
        // Use user's city location to restrict search to current city
        placesRepository.searchPlaces(
            query = query,
            latitude = state.userCityLat ?: state.mapCenterLat,
            longitude = state.userCityLng ?: state.mapCenterLng,
            radiusMeters = state.searchRadiusMeters,
            prioritizeSports = true
        )
            .onSuccess { results ->
                _uiState.update {
                    it.copy(
                        searchResults = results,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            }
            .onFailure { exception ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
            }
    }
    
    /**
     * Set user's current city location for search restriction
     */
    fun setUserLocation(latitude: Double, longitude: Double) {
        _uiState.update {
            it.copy(
                userCityLat = latitude,
                userCityLng = longitude,
                mapCenterLat = latitude,
                mapCenterLng = longitude
            )
        }
    }
    
    /**
     * Update search radius (in meters)
     */
    fun setSearchRadius(radiusMeters: Int) {
        _uiState.update { it.copy(searchRadiusMeters = radiusMeters) }
    }
    
    /**
     * Fetch current location from device GPS
     */
    fun fetchCurrentLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isFetchingLocation = true) }
            
            try {
                val hasPermission = locationProvider.hasLocationPermission()
                _uiState.update { it.copy(hasLocationPermission = hasPermission) }
                
                if (hasPermission) {
                    val location = locationProvider.getCurrentLocation()
                    if (location != null) {
                        _uiState.update {
                            it.copy(
                                userCityLat = location.latitude,
                                userCityLng = location.longitude,
                                mapCenterLat = location.latitude,
                                mapCenterLng = location.longitude,
                                isFetchingLocation = false
                            )
                        }
                    } else {
                        _uiState.update { 
                            it.copy(
                                isFetchingLocation = false,
                                errorMessage = "Could not get current location"
                            ) 
                        }
                    }
                } else {
                    _uiState.update { 
                        it.copy(
                            isFetchingLocation = false,
                            errorMessage = "Location permission required"
                        ) 
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isFetchingLocation = false,
                        errorMessage = "Error getting location: ${e.message}"
                    ) 
                }
            }
        }
    }

    fun clearSearch() {
        _uiState.update {
            it.copy(
                searchQuery = "",
                searchResults = emptyList(),
                isLoading = false
            )
        }
        searchQueryFlow.value = ""
    }

    fun selectLocation(location: PlaceSearchResult) {
        val state = _uiState.value
        
        // Add distance if we have user location and place has coordinates
        val locationWithDistance = if (state.userCityLat != null && state.userCityLng != null &&
            location.latitude != null && location.longitude != null) {
            placesRepository.withDistance(location, state.userCityLat, state.userCityLng)
        } else {
            location
        }
        
        _uiState.update {
            it.copy(
                selectedLocation = locationWithDistance,
                mapCenterLat = locationWithDistance.latitude ?: it.mapCenterLat,
                mapCenterLng = locationWithDistance.longitude ?: it.mapCenterLng
            )
        }

        // If location doesn't have coordinates, fetch place details with distance
        if (location.latitude == null || location.longitude == null) {
            viewModelScope.launch {
                placesRepository.getPlaceDetails(
                    placeId = location.placeId,
                    userLat = state.userCityLat,
                    userLng = state.userCityLng
                ).onSuccess { detailedPlace ->
                    _uiState.update {
                        it.copy(
                            selectedLocation = detailedPlace,
                            mapCenterLat = detailedPlace.latitude ?: it.mapCenterLat,
                            mapCenterLng = detailedPlace.longitude ?: it.mapCenterLng
                        )
                    }
                }
            }
        }
    }

    fun onMapClick(latitude: Double, longitude: Double) {
        val state = _uiState.value
        
        // Calculate distance if we have user location
        val distance = if (state.userCityLat != null && state.userCityLng != null) {
            placesRepository.withDistance(
                PlaceSearchResult(
                    placeId = "",
                    name = "Selected Location",
                    address = "",
                    latitude = latitude,
                    longitude = longitude
                ),
                state.userCityLat,
                state.userCityLng
            ).distanceMeters
        } else null
        
        _uiState.update {
            it.copy(
                selectedLocation = PlaceSearchResult(
                    placeId = "",
                    name = "Selected Location",
                    address = "Lat: ${String.format("%.4f", latitude)}, Lng: ${String.format("%.4f", longitude)}",
                    latitude = latitude,
                    longitude = longitude,
                    distanceMeters = distance
                ),
                mapCenterLat = latitude,
                mapCenterLng = longitude
            )
        }
    }
}
