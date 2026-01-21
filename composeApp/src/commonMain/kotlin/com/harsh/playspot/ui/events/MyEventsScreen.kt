package com.harsh.playspot.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.SetStatusBarAppearance
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.EventStatus
import com.harsh.playspot.dao.Venue
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
fun MyEventsScreenRoute(
    onBackPressed: () -> Unit,
    onCreateEventClick: () -> Unit,
    onEventClick: (String) -> Unit = {},
    viewModel: MyEventsViewModel = viewModel { MyEventsViewModel() }
) {
    SetStatusBarAppearance(isDarkTheme = isSystemInDarkTheme())
    val uiState by viewModel.uiState.collectAsState()
    val tabs = listOf("Organizing", "Participating")
    MyEventsScreen(
        tabs,
        onBackPressed,
        onCreateEventClick,
        onEventClick,
        viewModel::refreshEvents,
        onTabSelected = { index -> viewModel.onTabSelected(index) },
        uiState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEventsScreen(
    tabs: List<String>,
    onBackPressed: () -> Unit = {},
    onCreateEventClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
    onRefresh: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    uiState: MyEventsUiState,
) {

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TitleMedium(
                            text = "My Events",
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
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onCreateEventClick,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = "Create Event"
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Tab Row
                TabRow(
                    selectedTabIndex = uiState.selectedTabIndex,
                    containerColor = MaterialTheme.extendedColors.widgetBg,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[uiState.selectedTabIndex]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = uiState.selectedTabIndex == index,
                            onClick = { onTabSelected(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (uiState.selectedTabIndex == index)
                                        FontWeight.Bold else FontWeight.Medium,
                                    color = if (uiState.selectedTabIndex == index)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        )
                    }
                }

                // Content based on selected tab
                PullToRefreshBox(
                    isRefreshing = uiState.isLoading,
                    onRefresh = onRefresh,
                    modifier = Modifier.fillMaxSize()
                ) {
                    when (uiState.selectedTabIndex) {
                        0 -> OrganizingTabContent(
                            events = uiState.organizingEvents,
                            isLoading = uiState.isLoading,
                            errorMessage = uiState.errorMessage,
                            onEventClick = onEventClick,
                            onCreateEventClick = onCreateEventClick
                        )

                        1 -> ParticipatingTabContent(
                            events = uiState.participatingEvents,
                            isLoading = uiState.isLoading,
                            errorMessage = uiState.errorMessage,
                            onEventClick = onEventClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OrganizingTabContent(
    events: List<Event>,
    isLoading: Boolean,
    errorMessage: String?,
    onEventClick: (String) -> Unit,
    onCreateEventClick: () -> Unit
) {
    if (isLoading && events.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (errorMessage != null && events.isEmpty()) {
        EmptyStateMessage(
            message = errorMessage,
            isError = true
        )
    } else if (events.isEmpty()) {
        EmptyOrganizingState(onCreateEventClick = onCreateEventClick)
    } else {
        EventsList(
            events = events,
            onEventClick = onEventClick
        )
    }
}

@Composable
private fun ParticipatingTabContent(
    events: List<Event>,
    isLoading: Boolean,
    errorMessage: String?,
    onEventClick: (String) -> Unit
) {
    if (isLoading && events.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else if (errorMessage != null && events.isEmpty()) {
        EmptyStateMessage(
            message = errorMessage,
            isError = true
        )
    } else if (events.isEmpty()) {
        EmptyStateMessage(
            message = "You haven't joined any events yet.\nExplore and join events to see them here!",
            isError = false
        )
    } else {
        EventsList(
            events = events,
            onEventClick = onEventClick
        )
    }
}

@Composable
private fun EventsList(
    events: List<Event>,
    onEventClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(Padding.padding16Dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event.id) }
            )
        }

        // Bottom spacing for FAB
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun EventCard(
    event: Event,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)
    val statusColor = when (event.status) {
        EventStatus.UPCOMING -> Color(0xFF16A34A)
        EventStatus.ONGOING -> MaterialTheme.extendedColors.green
        EventStatus.COMPLETED -> MaterialTheme.colorScheme.outlineVariant
        EventStatus.CANCELLED -> MaterialTheme.extendedColors.red
        else -> MaterialTheme.colorScheme.outlineVariant
    }
    val statusLabel = when (event.status) {
        EventStatus.UPCOMING -> "Confirmed"
        EventStatus.ONGOING -> "Ongoing"
        EventStatus.COMPLETED -> "Completed"
        EventStatus.CANCELLED -> "Cancelled"
        else -> event.status.replaceFirstChar { it.uppercase() }
    }

    val sportsMap = getSportsMap()
    val sportUi = sportsMap[event.sportType]

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
            .clickable { onClick() }
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
                    .background(
                        (sportUi?.color ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (sportUi != null) {
                    Icon(
                        imageVector = sportUi.icon,
                        contentDescription = null,
                        tint = sportUi.color,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.SportsScore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Event Details
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                BodyMedium(
                    text = event.matchName,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.extendedColors.textDark
                )

                // Location & Date/Time
                val locationText =
                    if (event.venue.name.isNotBlank()) event.venue.name else "No venue"
                val dateTimeText = buildString {
                    if (event.date.isNotBlank()) append(event.date)
                    if (event.time.isNotBlank()) {
                        if (isNotEmpty()) append(" • ")
                        append(event.time)
                    }
                }
                BodySmall(
                    text = "$locationText • $dateTimeText",
                    color = MaterialTheme.colorScheme.outlineVariant
                )

                // Player avatars or info
                if (event.playerLimit > 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        // Avatar stack
                        Row {
                            val avatarColors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFFF97316),
                                Color(0xFF22C55E)
                            )
                            val avatarLabels = listOf("A", "B", "C")
                            repeat(minOf(3, event.currentPlayers)) { index ->
                                Box(
                                    modifier = Modifier
                                        .offset(x = (-8 * index).dp)
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(avatarColors[index % 3])
                                        .border(
                                            width = 2.dp,
                                            color = MaterialTheme.extendedColors.widgetBg,
                                            shape = CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = avatarLabels[index % 3],
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
                        text = "Individual training",
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
                    .background(statusColor.copy(alpha = 0.1f))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = statusLabel,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    letterSpacing = 0.5.sp
                )
            }

            // Progress bar (only for multi-player events)
            if (event.playerLimit > 1) {
                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.width(80.dp)
                ) {
                    LabelSmall(
                        text = "${event.currentPlayers}/${event.playerLimit}",
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { event.currentPlayers.toFloat() / event.playerLimit },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = sportUi?.color ?: MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.extendedColors.outline,
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // Skill level badge
            if (event.skillLevel.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.extendedColors.outline)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = event.skillLevel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyOrganizingState(
    onCreateEventClick: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.SportsScore,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            }

            TitleMedium(
                text = "No Events Yet",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )

            BodyMedium(
                text = "Start organizing your first sports event!\nBring people together for a great game.",
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable { onCreateEventClick() }
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    LabelLarge(
                        text = "Create Event",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateMessage(
    message: String,
    isError: Boolean
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            BodyMedium(
                text = message,
                color = if (isError) MaterialTheme.extendedColors.red
                else MaterialTheme.colorScheme.outlineVariant
            )
        }
    }
}

@Preview
@Composable
fun EventCardPreview() {
    val sampleEvent = Event(
        id = "1",
        matchName = "5-a-side Football",
        sportType = "Football",
        date = "Sat, 18 Jan",
        time = "19:00",
        playerLimit = 10,
        currentPlayers = 7,
        skillLevel = "Intermediate",
        status = EventStatus.UPCOMING,
        venue = Venue(
            name = "Downtown Arena",
            address = "123 Main St"
        )
    )

    AppTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            EventCard(
                event = sampleEvent,
                onClick = {}
            )
        }
    }
}

@Preview
@Composable
fun MyEventsScreenPreview() {
    MyEventsScreen(
        listOf("tab1, tab2"),
        uiState = MyEventsUiState(
            organizingEvents = listOf(
                Event(
                    matchName = "Badminton Match",
                    sportType = "Badminton",
                    date = "19 Jan 2026",
                    time = "19:00",
                    playerLimit = 10,
                    currentPlayers = 7,
                    skillLevel = "Intermediate",
                    status = EventStatus.UPCOMING,
                    venue = Venue(
                        name = "Shuttle eye academy",
                        address = "Downtown arena",
                        meetingPoint = "Enter from gate 2"
                    )
                )
            )
        )
    )
}

@Preview
@Composable
fun EmptyOrganizingStatePreview() {
    AppTheme {
        EmptyOrganizingState(onCreateEventClick = {})
    }
}
