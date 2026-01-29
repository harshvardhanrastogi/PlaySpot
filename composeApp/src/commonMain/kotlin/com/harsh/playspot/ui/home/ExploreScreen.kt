package com.harsh.playspot.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import coil3.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import com.harsh.playspot.ui.signup.LocationPermissionScreen
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Platform-specific location permission screen for Explore tab
 */
@Composable
expect fun ExploreLocationPermissionScreen(
    onPermissionGranted: () -> Unit,
    onSkip: () -> Unit
)

// Data classes for Explore screen
data class TrendingMatch(
    val id: String,
    val title: String,
    val sport: String,
    val sportIcon: ImageVector,
    val sportColor: Color,
    val date: String,
    val location: String,
    val status: MatchStatus,
    val imageUrl: String = ""
)

data class RecommendedMatch(
    val id: String,
    val title: String,
    val sport: String,
    val sportColor: Color,
    val date: String,
    val location: String,
    val distance: String,
    val tag: String, // "Friendly", "FREE", "$15", etc.
    val tagIsPrimary: Boolean = false,
    val status: MatchStatus,
    val attendees: Int = 0,
    val maxAttendees: Int = 0,
    val tags: List<String> = emptyList(),
    val coverImageUrl: String = "",
    val participantAvatars: List<String> = emptyList() // Optimized avatar URLs for participants
)

sealed class MatchStatus {
    data class SpotsLeft(val count: Int) : MatchStatus()
    data object Full : MatchStatus()
    data object Open : MatchStatus()
    data class Attending(val current: Int, val max: Int) : MatchStatus()
    data class NeedPlayer(val role: String) : MatchStatus()
}

data class FilterChip(
    val label: String,
    val icon: ImageVector? = null,
    val iconColor: Color = Color.Unspecified
)

@Composable
fun ExploreScreen(
    onEventClick: (String) -> Unit = {},
    viewModel: ExploreViewModel = viewModel { ExploreViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Show location permission screen if permission not granted
    if (!uiState.isCheckingPermission && uiState.hasLocationPermission == false) {
        ExploreLocationPermissionScreen(
            onPermissionGranted = viewModel::onLocationPermissionGranted,
            onSkip = viewModel::onLocationPermissionSkipped
        )
    } else {
        ExploreScreenContent(
            recommendedMatches = uiState.recommendedMatches,
            isLoading = uiState.isLoading || uiState.isCheckingPermission,
            userLocation = uiState.userLocationDisplay,
            onRefresh = viewModel::loadEvents,
            onEventClick = onEventClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreScreenContent(
    recommendedMatches: List<RecommendedMatch>,
    isLoading: Boolean = false,
    userLocation: String = "",
    onRefresh: () -> Unit = {},
    onEventClick: (String) -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All Sports") }
    
    val filters = listOf(
        FilterChip("All Sports"),
        FilterChip("Near Me", Icons.Filled.NearMe, Color(0xFF10B981)),
        FilterChip("Date", Icons.Filled.CalendarMonth, Color(0xFF8B5CF6)),
        FilterChip("Soccer", Icons.Filled.SportsSoccer),
        FilterChip("Basketball", Icons.Filled.SportsBasketball),
        FilterChip("Tennis", Icons.Filled.SportsTennis)
    )
    
    val trendingMatches = listOf(
        TrendingMatch(
            id = "1",
            title = "Downtown 7v7 League Final",
            sport = "Soccer",
            sportIcon = Icons.Filled.SportsSoccer,
            sportColor = Color(0xFF3B82F6),
            date = "Oct 24 • 8 PM",
            location = "City Arena Field",
            status = MatchStatus.SpotsLeft(1)
        ),
        TrendingMatch(
            id = "2",
            title = "Corporate League Mixer",
            sport = "Basketball",
            sportIcon = Icons.Filled.SportsBasketball,
            sportColor = Color(0xFFF97316),
            date = "Nov 12 • 6:30 PM",
            location = "Tech Gym Center",
            status = MatchStatus.Full
        ),
        TrendingMatch(
            id = "3",
            title = "Park Run & Food Trucks",
            sport = "Social Run",
            sportIcon = Icons.Filled.DirectionsRun,
            sportColor = Color(0xFF10B981),
            date = "Oct 25 • 10 AM",
            location = "Central Park Loop",
            status = MatchStatus.Open
        )
    )
    
    PullToRefreshBox(
        isRefreshing = isLoading,
        onRefresh = onRefresh,
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
        // Header
        item {
            ExploreHeader(userLocation = userLocation)
        }
        
        // Search Bar
        item {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onFilterClick = { /* TODO: Open filter dialog */ }
            )
        }
        
        // Filter Chips
        item {
            FilterChips(
                filters = filters,
                selectedFilter = selectedFilter,
                onFilterSelected = { selectedFilter = it }
            )
        }
        
        // Trending Matches Section
        item {
            TrendingMatchesSection(matches = trendingMatches)
        }
        
        // Divider
        item {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = Padding.padding16Dp),
                color = MaterialTheme.extendedColors.outline
            )
        }
        
        // Recommended Section Header
        item {
            RecommendedHeader()
        }
        
        // Recommended Matches
        items(recommendedMatches) { match ->
            RecommendedMatchCard(
                match = match,
                onClick = { onEventClick(match.id) }
            )
        }
        
            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
private fun ExploreHeader(
    userLocation: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.padding16Dp)
            .padding(top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            LabelSmall(
                text = "FINDING MATCHES IN",
                color = MaterialTheme.colorScheme.outlineVariant,
                fontWeight = FontWeight.Medium,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: Location picker */ }
            ) {
                TitleMedium(
                    text = userLocation.ifBlank { "Select your city" },
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textDark
                )
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Change location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        
        BadgedBox(
            badge = {
                Badge(
                    containerColor = Color(0xFFF97316),
                    modifier = Modifier.size(10.dp)
                )
            }
        ) {
            IconButton(
                onClick = { /* TODO: Notifications */ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "Notifications",
                    tint = MaterialTheme.extendedColors.textDark
                )
            }
        }
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onFilterClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.padding16Dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Search Input
        Box(
            modifier = Modifier
                .weight(1f)
                .height(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(22.dp)
                )
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.extendedColors.textDark
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (query.isEmpty()) {
                            BodyMedium(
                                text = "Search sports, venues, teams...",
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
        
        // Filter Button
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary)
                .clickable { onFilterClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "Filters",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun FilterChips(
    filters: List<FilterChip>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = Padding.padding16Dp)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEachIndexed { index, filter ->
            val isSelected = selectedFilter == filter.label
            
            // Add divider after Date chip
            if (index == 3) {
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(MaterialTheme.extendedColors.outline)
                        .align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary
                        else MaterialTheme.extendedColors.widgetBg
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = RoundedCornerShape(50)
                    )
                    .clickable { onFilterSelected(filter.label) }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    filter.icon?.let { icon ->
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isSelected) Color.White 
                                   else if (filter.iconColor != Color.Unspecified) filter.iconColor 
                                   else MaterialTheme.colorScheme.outlineVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    LabelLarge(
                        text = filter.label,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.extendedColors.textDark
                    )
                }
            }
        }
    }
}

@Composable
private fun TrendingMatchesSection(matches: List<TrendingMatch>) {
    Column(
        modifier = Modifier.padding(vertical = 16.dp)
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Padding.padding16Dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleMedium(
                text = "Trending Matches",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { /* TODO: See all */ }
            ) {
                LabelLarge(
                    text = "See all",
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        // Horizontal scroll of cards
        LazyRow(
            contentPadding = PaddingValues(horizontal = Padding.padding16Dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(matches) { match ->
                TrendingMatchCard(match = match)
            }
        }
    }
}

@Composable
private fun TrendingMatchCard(match: TrendingMatch) {
    val shape = RoundedCornerShape(24.dp)
    
    Column(
        modifier = Modifier
            .width(280.dp)
            .clickable { /* TODO: Open match details */ }
    ) {
        // Image with overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(4f / 3f)
                .clip(shape)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            match.sportColor.copy(alpha = 0.3f),
                            match.sportColor
                        )
                    )
                )
        ) {
            // Sport badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(match.sportColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = match.sportIcon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                    LabelSmall(
                        text = match.sport.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
            
            // Favorite button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.extendedColors.widgetBg.copy(alpha = 0.9f))
                    .clickable { /* TODO: Favorite */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // Match details
        TitleMedium(
            text = match.title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Date badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline,
                    shape = RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CalendarMonth,
                    contentDescription = null,
                    tint = MaterialTheme.extendedColors.purple,
                    modifier = Modifier.size(14.dp)
                )
                LabelSmall(
                    text = match.date,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Location and status
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(16.dp)
                )
                BodySmall(
                    text = match.location,
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
            
            MatchStatusBadge(status = match.status)
        }
    }
}

@Composable
private fun MatchStatusBadge(status: MatchStatus) {
    val (text, textColor, bgColor) = when (status) {
        is MatchStatus.SpotsLeft -> Triple(
            "${status.count} spot${if (status.count > 1) "s" else ""} left",
            MaterialTheme.extendedColors.orange,
            MaterialTheme.extendedColors.orangeContainer
        )
        is MatchStatus.Full -> Triple(
            "Full",
            MaterialTheme.extendedColors.emerald,
            MaterialTheme.extendedColors.emeraldContainer
        )
        is MatchStatus.Open -> Triple(
            "Open",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
        is MatchStatus.Attending -> Triple(
            "${status.current}/${status.max} Attending",
            MaterialTheme.extendedColors.emerald,
            MaterialTheme.extendedColors.emeraldContainer
        )
        is MatchStatus.NeedPlayer -> Triple(
            status.role,
            MaterialTheme.extendedColors.red,
            MaterialTheme.extendedColors.red.copy(alpha = 0.1f)
        )
    }
    
    val icon = when (status) {
        is MatchStatus.SpotsLeft -> Icons.Filled.Group
        is MatchStatus.Full -> Icons.Filled.CheckCircle
        is MatchStatus.Open -> Icons.Filled.Groups
        is MatchStatus.Attending -> null
        is MatchStatus.NeedPlayer -> Icons.Filled.PersonAdd
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(14.dp)
                )
            }
            LabelSmall(
                text = text,
                color = textColor,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun RecommendedHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.padding16Dp)
            .padding(top = 16.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TitleMedium(
            text = "Recommended for You",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )
        
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline,
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable { /* TODO: Sort */ }
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LabelSmall(
                    text = "Sort by",
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
                Icon(
                    imageVector = Icons.Filled.Sort,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun RecommendedMatchCard(
    match: RecommendedMatch,
    horizontalPadding: Dp = Padding.padding16Dp,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(24.dp)
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = horizontalPadding)
            .padding(bottom = 12.dp)
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cover image or sport icon placeholder
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            match.sportColor.copy(alpha = 0.3f),
                            match.sportColor
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            if (match.coverImageUrl.isNotBlank()) {
                AsyncImage(
                    model = match.coverImageUrl,
                    contentDescription = match.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Show sport icon when no cover image
                val sportUi = getSportsMap()[match.sport]
                sportUi?.let {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = match.sport,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
        
        // Match details
        Column(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 112.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Title and bookmark
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    BodyMedium(
                        text = match.title,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier
                            .size(22.dp)
                            .clickable { /* TODO: Bookmark */ }
                    )
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Sport tag and date
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(match.sportColor.copy(alpha = 0.1f))
                            .border(
                                width = 1.dp,
                                color = match.sportColor.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        LabelSmall(
                            text = match.sport,
                            fontWeight = FontWeight.Bold,
                            color = match.sportColor,
                            fontSize = 10.sp
                        )
                    }
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(4.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.outlineVariant)
                        )
                        LabelSmall(
                            text = match.date,
                            color = MaterialTheme.colorScheme.outlineVariant,
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(4.dp))
                
                // Location
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    LabelSmall(
                        text = "${match.location} • ${match.distance}",
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                }
            }
            
            // Bottom row: avatars + status
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar stack with actual participant images
                if (match.attendees > 0 || match.participantAvatars.isNotEmpty()) {
                    val fallbackColors = listOf(Color(0xFF3B82F6), Color(0xFFF97316), Color(0xFF22C55E), Color(0xFF8B5CF6))
                    // Use max of attendees count or actual avatars available
                    val avatarCount = maxOf(match.attendees, match.participantAvatars.size)
                    Row {
                        // Show up to 3 avatars
                        val avatarsToShow = minOf(3, avatarCount)
                        repeat(avatarsToShow) { index ->
                            val avatarUrl = match.participantAvatars.getOrNull(index)
                            Box(
                                modifier = Modifier
                                    .offset(x = (-6 * index).dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(fallbackColors[index % fallbackColors.size])
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.extendedColors.widgetBg,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!avatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = avatarUrl,
                                        contentDescription = "Participant",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    // Fallback to initials
                                    LabelSmall(
                                        text = listOf("A", "B", "C", "D")[index % 4],
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        // Show +N if more than 3 attendees
                        if (avatarCount > 3) {
                            Box(
                                modifier = Modifier
                                    .offset(x = (-18).dp)
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.extendedColors.widgetBg)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.extendedColors.outline,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                LabelSmall(
                                    text = "+${avatarCount - 3}",
                                    color = MaterialTheme.colorScheme.outlineVariant,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                
                MatchStatusBadgeSmall(status = match.status)
            }
        }
    }
}

@Composable
private fun MatchStatusBadgeSmall(status: MatchStatus) {
    val (text, textColor, bgColor) = when (status) {
        is MatchStatus.SpotsLeft -> Triple(
            "${status.count} spot${if (status.count > 1) "s" else ""} left",
            MaterialTheme.extendedColors.orange,
            MaterialTheme.extendedColors.orangeContainer
        )
        is MatchStatus.Full -> Triple(
            "Full",
            MaterialTheme.extendedColors.emerald,
            MaterialTheme.extendedColors.emeraldContainer
        )
        is MatchStatus.Open -> Triple(
            "Open",
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
        )
        is MatchStatus.Attending -> Triple(
            "${status.current}/${status.max} Attending",
            MaterialTheme.extendedColors.emerald,
            MaterialTheme.extendedColors.emeraldContainer
        )
        is MatchStatus.NeedPlayer -> Triple(
            status.role,
            MaterialTheme.extendedColors.red,
            MaterialTheme.extendedColors.red.copy(alpha = 0.1f)
        )
    }
    
    val icon = when (status) {
        is MatchStatus.NeedPlayer -> Icons.Filled.PersonAdd
        else -> null
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(12.dp)
                )
            }
            LabelSmall(
                text = text,
                fontWeight = FontWeight.Bold,
                color = textColor,
                fontSize = 11.sp
            )
        }
    }
}

@Preview
@Composable
fun ExploreScreenPreview() {
    val sampleRecommendedMatches = listOf(
        RecommendedMatch(
            id = "1",
            title = "Morning Tennis Doubles",
            sport = "Tennis",
            sportColor = Color(0xFF3B82F6),
            date = "Tomorrow • 6:30 AM",
            location = "Mission Bay Courts",
            distance = "0.5mi",
            tag = "Friendly",
            status = MatchStatus.NeedPlayer("Need 1 player"),
            attendees = 3
        ),
        RecommendedMatch(
            id = "2",
            title = "Lunchtime Ping Pong",
            sport = "Table Tennis",
            sportColor = Color(0xFF8B5CF6),
            date = "Wed, Oct 29 • 12:30 PM",
            location = "Office Rec Room",
            distance = "0.1mi",
            tag = "FREE",
            tagIsPrimary = true,
            status = MatchStatus.Attending(4, 8),
            tags = listOf("Casual", "Mixed")
        ),
        RecommendedMatch(
            id = "3",
            title = "Indoor Volleyball Pickup",
            sport = "Volleyball",
            sportColor = Color(0xFFF97316),
            date = "Fri, Oct 31 • 6:00 PM",
            location = "SF Sports Center",
            distance = "3.0mi",
            tag = "$15",
            status = MatchStatus.SpotsLeft(2),
            attendees = 2
        ),
        RecommendedMatch(
            id = "4",
            title = "Evening Futsal 5v5",
            sport = "Soccer",
            sportColor = Color(0xFF3B82F6),
            date = "Sat, Nov 1 • 8:30 PM",
            location = "The Pitch",
            distance = "1.5mi",
            tag = "$10",
            status = MatchStatus.NeedPlayer("Need GK"),
            tags = listOf("Interm.", "Turf")
        )
    )
    
    ExploreScreenContent(
        recommendedMatches = sampleRecommendedMatches
    )
}

@Preview(showBackground = true)
@Composable
fun RecommendedMatchCardPreview() {
    Column(
        modifier = Modifier.background(Color(0xFFF5F5F5)),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // With cover image placeholder, spots left
        RecommendedMatchCard(
            match = RecommendedMatch(
                id = "1",
                title = "Morning Tennis Doubles",
                sport = "Tennis",
                sportColor = Color(0xFF3B82F6),
                date = "Tomorrow • 6:30 AM",
                location = "Mission Bay Courts",
                distance = "0.5mi",
                tag = "Intermediate",
                tagIsPrimary = false,
                status = MatchStatus.SpotsLeft(2),
                attendees = 6,
                maxAttendees = 8,
                tags = listOf("Intermediate"),
                coverImageUrl = ""
            ),
            onClick = {}
        )

        // Full match
        RecommendedMatchCard(
            match = RecommendedMatch(
                id = "2",
                title = "5-a-side Football",
                sport = "Football",
                sportColor = Color(0xFF22C55E),
                date = "Sat, Oct 26 • 4:00 PM",
                location = "Downtown Arena",
                distance = "1.2mi",
                tag = "Advanced",
                tagIsPrimary = false,
                status = MatchStatus.Full,
                attendees = 10,
                maxAttendees = 10,
                tags = listOf("Advanced"),
                coverImageUrl = ""
            ),
            onClick = {}
        )

        // Organizing tag (creator)
        RecommendedMatchCard(
            match = RecommendedMatch(
                id = "3",
                title = "Basketball Pickup " +
                        "\nGame",
                sport = "Basketball",
                sportColor = Color(0xFFF97316),
                date = "Sun, Oct 27 • 10:00 AM",
                location = "Central Park Courts",
                distance = "0.8mi",
                tag = "Organizing",
                tagIsPrimary = true,
                status = MatchStatus.Attending(4, 10),
                attendees = 4,
                maxAttendees = 10,
                tags = listOf("Beginner"),
                coverImageUrl = ""
            ),
            onClick = {}
        )
    }
}
