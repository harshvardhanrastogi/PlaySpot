package com.harsh.playspot.ui.events

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.data.places.PlaceSearchResult
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.map.GoogleMapView
import com.harsh.playspot.ui.map.MapLocation
import org.jetbrains.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionScreen(
    onBackPressed: () -> Unit,
    onLocationSelected: (name: String, address: String) -> Unit,
    // User's city coordinates to restrict search area
    cityLatitude: Double? = null,
    cityLongitude: Double? = null,
    viewModel: LocationSelectionViewModel = viewModel { LocationSelectionViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Fetch current location on screen launch
    LaunchedEffect(Unit) {
        if (cityLatitude != null && cityLongitude != null) {
            // Use provided city coordinates
            viewModel.setUserLocation(cityLatitude, cityLongitude)
        } else {
            // Fetch current device location
            viewModel.fetchCurrentLocation()
        }
    }

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TitleMedium(
                            text = "Select Location",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.textDark
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBackPressed) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.extendedColors.widgetBg
                    )
                )
            }
        ) { paddingValues ->
            // Track search bar focus state
            val searchInteractionSource = remember { MutableInteractionSource() }
            val isFocused by searchInteractionSource.collectIsFocusedAsState()
            
            // Detect keyboard visibility using WindowInsets
            val density = LocalDensity.current
            val imeBottom = WindowInsets.ime.getBottom(density)
            val isKeyboardVisible = imeBottom > 0
            
            // Animate map height - shrinks when keyboard is visible
            val mapHeight by animateDpAsState(
                targetValue = if (isKeyboardVisible) 140.dp else 260.dp,
                animationSpec = tween(durationMillis = 300),
                label = "mapHeight"
            )
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Search Bar - always at top, fixed position
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = Padding.padding16Dp)
                        .padding(top = 12.dp, bottom = 12.dp)
                ) {
                    SearchBar(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChange,
                        onClear = viewModel::clearSearch,
                        isLoading = uiState.isLoading,
                        interactionSource = searchInteractionSource
                    )
                }
                
                // Google Map Section with My Location button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(mapHeight)
                ) {
                    GoogleMapView(
                        modifier = Modifier.fillMaxSize(),
                        initialLocation = MapLocation(
                            latitude = uiState.mapCenterLat,
                            longitude = uiState.mapCenterLng,
                            name = uiState.selectedLocation?.name ?: "",
                            address = uiState.selectedLocation?.address ?: ""
                        ),
                        markers = uiState.selectedLocation?.let { selected ->
                            if (selected.latitude != null && selected.longitude != null) {
                                listOf(
                                    MapLocation(
                                        latitude = selected.latitude,
                                        longitude = selected.longitude,
                                        name = selected.name,
                                        address = selected.address
                                    )
                                )
                            } else emptyList()
                        } ?: emptyList(),
                        onMapClick = { location ->
                            viewModel.onMapClick(location.latitude, location.longitude)
                        },
                        onMarkerClick = { location ->
                            // Marker clicked - could show info
                        }
                    )
                    
                    // My Location Button
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.extendedColors.widgetBg)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.extendedColors.outline,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.fetchCurrentLocation() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (uiState.isFetchingLocation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Filled.MyLocation,
                                contentDescription = "My Location",
                                tint = if (uiState.userCityLat != null) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outlineVariant,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Search Results List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Padding.padding16Dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Results Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (uiState.searchQuery.isNotBlank()) "SEARCH RESULTS" else "SEARCH FOR VENUES",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    letterSpacing = 1.sp
                                )
                                if (uiState.userCityLat != null) {
                                    Text(
                                        text = "Showing nearby venues only",
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                }
                            }
                            if (uiState.searchResults.isNotEmpty()) {
                                BodySmall(
                                    text = "${uiState.searchResults.size} results found",
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }

                    // Error Message
                    if (uiState.errorMessage != null) {
                        item {
                            BodyMedium(
                                text = uiState.errorMessage ?: "An error occurred",
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }

                    // Empty state
                    if (uiState.searchQuery.isBlank() && uiState.searchResults.isEmpty()) {
                        item {
                            EmptySearchState()
                        }
                    }

                    // Selected Location Card (if selected from map)
                    uiState.selectedLocation?.let { selected ->
                        if (selected.placeId.isEmpty()) { // Selected from map tap
                            item {
                                SelectedLocationCard(
                                    location = selected,
                                    onConfirm = {
                                        onLocationSelected(selected.name, selected.address)
                                    }
                                )
                            }
                        }
                    }

                    // Search Results
                    items(uiState.searchResults) { place ->
                        PlaceCard(
                            place = place,
                            isSelected = uiState.selectedLocation?.placeId == place.placeId,
                            onSelect = {
                                viewModel.selectLocation(place)
                            },
                            onConfirm = {
                                onLocationSelected(place.name, place.address)
                            }
                        )
                    }

                    // Bottom spacing
                    item {
                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptySearchState() {
    val shape = RoundedCornerShape(16.dp)
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(48.dp)
        )
        BodyMedium(
            text = "Search for a venue or location",
            color = MaterialTheme.colorScheme.outlineVariant
        )
        BodySmall(
            text = "Type a venue name, address, or area to find sports locations",
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
        )
    }
}


@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onClear: () -> Unit = {},
    isLoading: Boolean = false,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val shape = RoundedCornerShape(16.dp)
    val isFocused by interactionSource.collectIsFocusedAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = if (isFocused || query.isNotBlank()) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = if (isFocused || query.isNotBlank()) MaterialTheme.colorScheme.primary 
                   else MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(24.dp)
        )
        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.extendedColors.textDark
            ),
            modifier = Modifier.weight(1f),
            singleLine = true,
            interactionSource = interactionSource,
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search for a venue or location",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )
        
        // Loading or Clear button
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else if (query.isNotBlank()) {
            Icon(
                imageVector = Icons.Filled.Clear,
                contentDescription = "Clear search",
                tint = MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onClear() }
            )
        }
    }
}

@Composable
private fun PlaceCard(
    place: PlaceSearchResult,
    isSelected: Boolean = false,
    onSelect: () -> Unit,
    onConfirm: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                else MaterialTheme.extendedColors.widgetBg
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary 
                       else MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .clickable { onSelect() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Place Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = place.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark,
                        maxLines = 1,
                        modifier = Modifier.weight(1f)
                    )
                    // Distance badge
                    place.getFormattedDistance()?.let { distance ->
                        Text(
                            text = distance,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
                if (place.address.isNotBlank()) {
                    Text(
                        text = place.address,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        maxLines = 2
                    )
                }
            }
        }

        // Select/Confirm Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) MaterialTheme.extendedColors.green
                    else MaterialTheme.colorScheme.primary
                )
                .clickable { 
                    if (isSelected) onConfirm() else onSelect()
                }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LabelLarge(
                text = if (isSelected) "Confirm" else "Select",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
private fun SelectedLocationCard(
    location: PlaceSearchResult,
    onConfirm: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.green.copy(alpha = 0.1f))
            .border(
                width = 2.dp,
                color = MaterialTheme.extendedColors.green,
                shape = shape
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.extendedColors.green.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.green,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Location Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📍 Selected from Map",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.green,
                        letterSpacing = 0.5.sp
                    )
                    // Distance badge
                    location.getFormattedDistance()?.let { distance ->
                        Text(
                            text = distance,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.extendedColors.green
                        )
                    }
                }
                Text(
                    text = location.address,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        // Confirm Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.extendedColors.green)
                .clickable { onConfirm() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LabelLarge(
                text = "Confirm",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

/**
 * Wrapper composable that provides full venue details including coordinates
 */
@Composable
fun LocationSelectionScreenWithDetails(
    onBackPressed: () -> Unit,
    onLocationSelected: (name: String, address: String, placeId: String, latitude: Double?, longitude: Double?) -> Unit,
    viewModel: LocationSelectionViewModel = viewModel { LocationSelectionViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LocationSelectionScreen(
        onBackPressed = onBackPressed,
        onLocationSelected = { name, address ->
            // Get selected location details from ViewModel state
            val selectedLocation = uiState.selectedLocation
            onLocationSelected(
                name,
                address,
                selectedLocation?.placeId ?: "",
                selectedLocation?.latitude,
                selectedLocation?.longitude
            )
        },
        viewModel = viewModel
    )
}

@Preview
@Composable
fun LocationSelectionScreenPreview() {
    LocationSelectionScreen(
        onBackPressed = {},
        onLocationSelected = { _, _ -> }
    )
}
