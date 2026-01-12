package com.harsh.playspot.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.SportUi
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import com.harsh.playspot.ui.core.semiCircleCornerShape
import org.jetbrains.compose.resources.stringResource
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.events_add
import playspot.composeapp.generated.resources.events_add_event
import playspot.composeapp.generated.resources.events_individual_training
import playspot.composeapp.generated.resources.events_notifications
import playspot.composeapp.generated.resources.events_see_all
import playspot.composeapp.generated.resources.events_suggested_for_you
import playspot.composeapp.generated.resources.events_tab_attending
import playspot.composeapp.generated.resources.events_tab_organizing
import playspot.composeapp.generated.resources.events_title
import playspot.composeapp.generated.resources.events_upcoming_matches

data class SportEvent(
    val sport: SportUi,
    val title: String,
    val location: String,
    val dateTime: String,
    val status: EventStatus,
    val currentPlayers: Int,
    val maxPlayers: Int,
    val memberAvatars: List<String> = emptyList()
)

enum class EventStatus(val label: String, val color: Color, val bgColor: Color) {
    CONFIRMED("Confirmed", Color(0xFF16A34A), Color(0xFF16A34A).copy(alpha = 0.1f)),
    PENDING("Pending", Color(0xFF6B7280), Color(0xFF6B7280).copy(alpha = 0.1f)),
    SOLO("Solo", Color(0xFF6B7280), Color(0xFF6B7280).copy(alpha = 0.1f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    onCreateEventClick: () -> Unit = {}
) {
    val tabAttending = stringResource(Res.string.events_tab_attending)
    val tabOrganizing = stringResource(Res.string.events_tab_organizing)
    var selectedTab by remember { mutableStateOf(tabAttending) }

    val football = getSportsMap()["Football"] ?: return
    val tennis = getSportsMap()["Tennis"] ?: return
    val swim = getSportsMap()["Swimming"] ?: return

    val sampleEvents = listOf(
        SportEvent(
            sport = football,
            title = "5-a-side Football",
            location = "Downtown Arena",
            dateTime = "Tonight 19:00",
            status = EventStatus.CONFIRMED,
            currentPlayers = 9,
            maxPlayers = 10
        ),
        SportEvent(
            sport = tennis,
            title = "Morning Tennis",
            location = "Green Park Courts",
            dateTime = "Sat, 12 Oct",
            status = EventStatus.PENDING,
            currentPlayers = 2,
            maxPlayers = 4
        ),
        SportEvent(
            sport = swim,
            title = "Weekly Swim",
            location = "City Pool",
            dateTime = "Tue, 15 Oct",
            status = EventStatus.SOLO,
            currentPlayers = 1,
            maxPlayers = 1
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Profile avatar with online indicator
                        Box {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "AJ",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            // Online indicator
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .align(Alignment.BottomEnd)
                                    .offset(x = 2.dp, y = 2.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF22C55E))
                                    .border(
                                        width = 2.dp,
                                        color = MaterialTheme.colorScheme.background,
                                        shape = CircleShape
                                    )
                            )
                        }
                        TitleMedium(
                            text = stringResource(Res.string.events_title),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.textDark
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = Color(0xFFEF4444),
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    ) {
                        IconButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = stringResource(Res.string.events_notifications),
                                tint = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateEventClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(Res.string.events_add_event),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = Padding.padding16Dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tab Switcher
            item {
                TabSwitcher(
                    tabs = listOf(tabAttending, tabOrganizing),
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }

            // Upcoming Matches Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TitleMedium(
                        text = stringResource(Res.string.events_upcoming_matches),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                    LabelLarge(
                        text = stringResource(Res.string.events_see_all),
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Event Cards
            items(sampleEvents) { event ->
                EventCard(event = event)
            }

            // Suggested For You Header
            item {
                Spacer(modifier = Modifier.height(8.dp))
                TitleMedium(
                    text = stringResource(Res.string.events_suggested_for_you),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textDark
                )
            }

            // Suggested Cards (Horizontal)
            item {
                SuggestedSection()
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}

@Composable
private fun TabSwitcher(
    tabs: List<String>,
    selectedTab: String,
    onTabSelected: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.extendedColors.outline.copy(alpha = 0.3f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isSelected) MaterialTheme.extendedColors.widgetBg
                        else Color.Transparent
                    ),
                contentAlignment = Alignment.Center
            ) {
                LabelLarge(
                    text = tab,
                    fontWeight = FontWeight.Medium,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun EventCard(event: SportEvent) {
    val shape = RoundedCornerShape(12.dp)

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
            .padding(Padding.padding16Dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sport Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(event.sport.color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = event.sport.icon,
                    contentDescription = null,
                    tint = event.sport.color,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Event Details
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BodyMedium(
                    text = event.title,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.extendedColors.textDark
                )
                BodySmall(
                    text = "${event.location} • ${event.dateTime}",
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Member avatars or info
                if (event.status != EventStatus.SOLO) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Avatar stack
                        Row {
                            repeat(minOf(3, event.currentPlayers)) { index ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (-8 * index).dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            listOf(
                                                Color(0xFF3B82F6),
                                                Color(0xFFF97316),
                                                Color(0xFF22C55E)
                                            )[index % 3]
                                        )
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.extendedColors.widgetBg,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = listOf("A", "B", "C")[index % 3],
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        if (event.currentPlayers > 3) {
                            LabelSmall(
                                text = "+${event.currentPlayers - 3} others",
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                        }
                    }
                } else {
                    LabelSmall(
                        text = stringResource(Res.string.events_individual_training),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }

        // Right side - Status and Progress
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Status Badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(event.status.bgColor)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = event.status.label,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = event.status.color,
                    letterSpacing = 0.5.sp
                )
            }

            // Progress bar (only for non-solo events)
            if (event.status != EventStatus.SOLO) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(80.dp)
                ) {
                    LabelSmall(
                        text = "${event.currentPlayers}/${event.maxPlayers}",
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { event.currentPlayers.toFloat() / event.maxPlayers },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = event.sport.color,
                        trackColor = MaterialTheme.extendedColors.outline,
                        strokeCap = StrokeCap.Round
                    )
                }
            }
        }
    }
}

@Composable
private fun SuggestedSection() {
    val suggestions = listOf(
        "Downtown Hoop Heads" to "2.5km away",
        "Weekend Runners" to "5km away"
    )

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(suggestions) { (title, distance) ->
            SuggestedCard(title = title, distance = distance)
        }
    }
}

@Composable
private fun SuggestedCard(title: String, distance: String) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .width(256.dp)
            .height(160.dp)
            .clip(shape)
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                        MaterialTheme.colorScheme.primary
                    )
                )
            )
    ) {
        // Content overlay
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LabelSmall(
                    text = "📍 $distance",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // Add button
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = stringResource(Res.string.events_add),
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
