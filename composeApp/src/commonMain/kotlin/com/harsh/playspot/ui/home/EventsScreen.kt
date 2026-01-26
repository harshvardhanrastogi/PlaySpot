package com.harsh.playspot.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.HandleSharedEvents
import com.harsh.playspot.ui.core.EmptyState
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.events.MyEventsViewModel
import org.jetbrains.compose.resources.stringResource
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.events_add
import playspot.composeapp.generated.resources.events_add_event
import playspot.composeapp.generated.resources.events_notifications
import playspot.composeapp.generated.resources.events_see_all
import playspot.composeapp.generated.resources.events_suggested_for_you
import playspot.composeapp.generated.resources.events_tab_attending
import playspot.composeapp.generated.resources.events_tab_organizing
import playspot.composeapp.generated.resources.events_title
import playspot.composeapp.generated.resources.events_upcoming_matches

enum class EventStatus(val label: String, val color: Color, val bgColor: Color) {
    CONFIRMED(
        "Confirmed",
        Color(0xFF16A34A),
        Color(0xFF16A34A).copy(alpha = 0.1f)
    ),
    PENDING("Pending", Color(0xFF6B7280), Color(0xFF6B7280).copy(alpha = 0.1f)), SOLO(
        "Solo",
        Color(0xFF6B7280),
        Color(0xFF6B7280).copy(alpha = 0.1f)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    openOrganizingEvents: Boolean,
    onCreateEventClick: () -> Unit = {},
    onEventClick: (String) -> Unit = {},
    onEventDetailsClick: (String) -> Unit = {},
    onExploreEventsClick: () -> Unit = {},
    viewModel: MyEventsViewModel = viewModel { MyEventsViewModel() }
) {
    HandleSharedEvents(viewModel)

    viewModel.setPreferredTab(openOrganizingEvents)
    val tabAttending = stringResource(Res.string.events_tab_attending)
    val tabOrganizing = stringResource(Res.string.events_tab_organizing)
    val eventsUiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableStateOf(if (eventsUiState.selectedTabIndex == 0) tabAttending else tabOrganizing) }

    val isOrganizingTab = selectedTab == tabOrganizing

    Scaffold(topBar = {
        TopAppBar(
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Profile avatar with online indicator
                    Box {
                        Box(
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                    shape = CircleShape
                                ), contentAlignment = Alignment.Center
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
                            modifier = Modifier.size(12.dp).align(Alignment.BottomEnd)
                                .offset(x = 2.dp, y = 2.dp).clip(CircleShape)
                                .background(Color(0xFF22C55E)).border(
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
            }, actions = {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = Color(0xFFEF4444), modifier = Modifier.size(10.dp)
                        )
                    }) {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = stringResource(Res.string.events_notifications),
                            tint = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.background
            )
        )
    }, floatingActionButton = {
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
    }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = eventsUiState.isLoading,
            onRefresh = { viewModel.refreshEvents() },
            modifier = Modifier.fillMaxSize().padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = Padding.padding16Dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tab Switcher
                item {
                    TabSwitcher(
                        tabs = listOf(tabAttending, tabOrganizing),
                        selectedTab = selectedTab,
                        onTabSelected = { selectedTab = it })
                }

                if (isOrganizingTab) {
                    // Organizing Tab Content - Show events created by the user

                    if (eventsUiState.isLoading && eventsUiState.organizingEvents.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth().height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    } else if (eventsUiState.organizingEvents.isEmpty()) {
                        item {
                            EmptyState(
                                title = "No events yet",
                                description = "Start organizing your first sports event!",
                                cta = "Create Event",
                                onClick = onCreateEventClick
                            )
                        }
                    } else {
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TitleMedium(
                                    text = "My Events",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.extendedColors.textDark
                                )
                            }
                        }

                        items(eventsUiState.organizingEvents) { match ->
                            RecommendedMatchCard(
                                match = match,
                                horizontalPadding = 0.dp,
                                onClick = { onEventClick(match.id) }
                            )
                        }
                    }
                } else {
                    // Attending Tab Content - Show user's participated events from USER_EVENTS collection

                    // Attending Event Cards using RecommendedMatchCard
                    if (eventsUiState.attendingEvents.isEmpty() && !eventsUiState.isLoading) {
                        item {
                            EmptyState(
                                title = "No events yet",
                                description = "Join events from Explore to see them here",
                                cta = "Explore events",
                                onClick = onExploreEventsClick
                            )
                        }
                    } else if (!eventsUiState.isLoading) {
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

                        items(eventsUiState.attendingEvents) { match ->
                            RecommendedMatchCard(
                                match = match,
                                horizontalPadding = 0.dp,
                                onClick = { onEventDetailsClick(match.id) }
                            )
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
                    }
                }

                // Bottom spacing
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
private fun TabSwitcher(
    tabs: List<String>, selectedTab: String, onTabSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(48.dp).clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.extendedColors.outline.copy(alpha = 0.3f)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEach { tab ->
            val isSelected = selectedTab == tab
            Box(
                modifier = Modifier.weight(1f).fillMaxSize().clip(RoundedCornerShape(8.dp))
                    .clickable { onTabSelected(tab) }.background(
                        if (isSelected) MaterialTheme.extendedColors.widgetBg
                        else Color.Transparent
                    ), contentAlignment = Alignment.Center
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
private fun SuggestedSection() {
    val suggestions = listOf(
        "Downtown Hoop Heads" to "2.5km away", "Weekend Runners" to "5km away"
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
        modifier = Modifier.width(256.dp).height(160.dp).clip(shape).background(
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
            modifier = Modifier.align(Alignment.BottomStart).padding(12.dp)
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
                    text = "📍 $distance", color = Color.White.copy(alpha = 0.8f),
                )
            }
        }

        // Add button
        Box(
            modifier = Modifier.align(Alignment.TopEnd).padding(12.dp).size(32.dp).clip(CircleShape)
                .background(Color.White.copy(alpha = 0.2f)), contentAlignment = Alignment.Center
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
