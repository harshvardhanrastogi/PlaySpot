package com.harsh.playspot.ui.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Stadium
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.skateboarder

data class Venue(
    val name: String,
    val address: String,
    val distance: String,
    val icon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSelectionScreen(
    onBackPressed: () -> Unit,
    onLocationSelected: (name: String, address: String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    
    val venues = remember {
        listOf(
            Venue(
                name = "Westside Community Center",
                address = "242 W 14th St, New York",
                distance = "0.8 miles away",
                icon = Icons.Filled.SportsSoccer
            ),
            Venue(
                name = "Riverside Turf",
                address = "725 Riverside Dr, New York",
                distance = "1.5 miles away",
                icon = Icons.Filled.Stadium
            ),
            Venue(
                name = "Elite Sports Arena",
                address = "560 W 42nd St, New York",
                distance = "2.1 miles away",
                icon = Icons.Filled.FitnessCenter
            ),
            Venue(
                name = "Central Court Club",
                address = "Park Ave & 66th St, New York",
                distance = "3.4 miles away",
                icon = Icons.Filled.SportsTennis
            )
        )
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Map Section
                MapSection()

                // Search and Venues List
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Padding.padding16Dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Bar
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        SearchBar(
                            query = searchQuery,
                            onQueryChange = { searchQuery = it }
                        )
                    }

                    // Nearby Venues Header
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "NEARBY VENUES",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                letterSpacing = 1.sp
                            )
                            BodySmall(
                                text = "${venues.size} venues found",
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }

                    // Venue Cards
                    items(venues.filter { 
                        searchQuery.isEmpty() || 
                        it.name.contains(searchQuery, ignoreCase = true) ||
                        it.address.contains(searchQuery, ignoreCase = true)
                    }) { venue ->
                        VenueCard(
                            venue = venue,
                            onSelect = {
                                onLocationSelected(venue.name, venue.address)
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
private fun MapSection() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(280.dp)
    ) {
        // Map placeholder image
        Image(
            painter = painterResource(Res.drawable.skateboarder),
            contentDescription = "Map",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        // Gradient overlay at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(60.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
        )

        // Map Markers
        MapMarker(
            name = "Westside Center",
            isSelected = true,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 60.dp, y = 80.dp)
        )

        MapMarker(
            name = "Riverside Turf",
            isSelected = false,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = (-40).dp, y = 140.dp)
        )

        // Map controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 70.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapControlButton(
                icon = Icons.Filled.MyLocation,
                onClick = { /* TODO: Center on location */ }
            )
            MapControlButton(
                icon = Icons.Filled.Layers,
                onClick = { /* TODO: Toggle map layers */ }
            )
        }
    }
}

@Composable
private fun MapMarker(
    name: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.extendedColors.widgetBg
                )
                .border(
                    width = 1.dp,
                    color = if (isSelected) Color.White.copy(alpha = 0.2f) 
                           else MaterialTheme.extendedColors.outline,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.SportsSoccer,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(14.dp)
            )
            Text(
                text = name,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else MaterialTheme.extendedColors.textDark
            )
        }
        // Pin stem
        Box(
            modifier = Modifier
                .width(2.dp)
                .height(12.dp)
                .background(
                    if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.extendedColors.widgetBg
                )
        )
    }
}

@Composable
private fun MapControlButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.extendedColors.textDark,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Search,
            contentDescription = "Search",
            tint = MaterialTheme.colorScheme.outlineVariant,
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
            decorationBox = { innerTextField ->
                if (query.isEmpty()) {
                    Text(
                        text = "Search for a location",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun VenueCard(
    venue: Venue,
    onSelect: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
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
            // Venue Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = venue.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Venue Details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = venue.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textDark,
                    maxLines = 1
                )
                BodySmall(
                    text = venue.address,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.NearMe,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.orange,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = venue.distance,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        // Select Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onSelect() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            LabelLarge(
                text = "Select",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview
@Composable
fun LocationSelectionScreenPreview() {
    LocationSelectionScreen(
        onBackPressed = {},
        onLocationSelected = { _, _ -> }
    )
}
