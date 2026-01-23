package com.harsh.playspot.ui.events

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.harsh.playspot.dao.Event
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.SportColors
import com.harsh.playspot.ui.core.TitleLarge
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EventDetailsScreenRoute(
    eventId: String,
    onBackPressed: () -> Unit,
    viewModel: EventDetailsViewModel = viewModel(key = eventId) {
        EventDetailsViewModel(eventId = eventId)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is EventDetailsEvent.JoinSuccess -> {
                    snackbarHostState.showSnackbar("You've joined the match!")
                }
                is EventDetailsEvent.LeaveSuccess -> {
                    snackbarHostState.showSnackbar("You've left the match")
                }
                is EventDetailsEvent.Error -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    EventDetailsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackPressed = onBackPressed,
        onJoinClick = viewModel::joinEvent,
        onLeaveClick = viewModel::leaveEvent,
        onFavoriteClick = { /* TODO */ },
        onShareClick = { /* TODO */ },
        onOpenMapClick = { /* TODO */ },
        onHostChatClick = { /* TODO */ }
    )
}

@Composable
private fun EventDetailsScreen(
    uiState: EventDetailsUiState,
    snackbarHostState: SnackbarHostState,
    onBackPressed: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenMapClick: () -> Unit,
    onHostChatClick: () -> Unit
) {
    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    }
                } else if (uiState.event != null) {
                    EventDetailsContent(
                        event = uiState.event,
                        coverImageUrl = uiState.coverImageUrl,
                        participants = uiState.participants,
                        isCurrentUserHost = uiState.isCurrentUserHost,
                        isCurrentUserParticipant = uiState.isCurrentUserParticipant,
                        spotsLeft = uiState.spotsLeft,
                        isFull = uiState.isFull,
                        isJoining = uiState.isJoining,
                        isLeaving = uiState.isLeaving,
                        onBackPressed = onBackPressed,
                        onJoinClick = onJoinClick,
                        onLeaveClick = onLeaveClick,
                        onFavoriteClick = onFavoriteClick,
                        onShareClick = onShareClick,
                        onOpenMapClick = onOpenMapClick,
                        onHostChatClick = onHostChatClick
                    )
                } else {
                    // Error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            TitleMedium(
                                text = uiState.error ?: "Event not found",
                                color = MaterialTheme.extendedColors.textDark
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.primary)
                                    .clickable { onBackPressed() }
                                    .padding(horizontal = 24.dp, vertical = 12.dp)
                            ) {
                                LabelLarge(text = "Go Back", color = Color.White)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EventDetailsContent(
    event: Event,
    coverImageUrl: String,
    participants: List<ParticipantUi>,
    isCurrentUserHost: Boolean,
    isCurrentUserParticipant: Boolean,
    spotsLeft: Int,
    isFull: Boolean,
    isJoining: Boolean,
    isLeaving: Boolean,
    onBackPressed: () -> Unit,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit,
    onOpenMapClick: () -> Unit,
    onHostChatClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val sportColor = SportColors.getColor(event.sportType)
    val sportsMap = getSportsMap()
    val sportUi = sportsMap[event.sportType]

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Cover Image Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
            ) {
                // Cover image or gradient placeholder
                if (coverImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = coverImageUrl,
                        contentDescription = event.matchName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        sportColor.copy(alpha = 0.3f),
                                        sportColor
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        sportUi?.let {
                            Icon(
                                imageVector = it.icon,
                                contentDescription = event.sportType,
                                tint = Color.White.copy(alpha = 0.5f),
                                modifier = Modifier.size(120.dp)
                            )
                        }
                    }
                }

                // Gradient overlays
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.4f),
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    MaterialTheme.colorScheme.background.copy(alpha = 0.8f),
                                    MaterialTheme.colorScheme.background
                                ),
                                startY = 200f
                            )
                        )
                )

                // Top bar with buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(top = 48.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back button
                    HeaderButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBackPressed
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        HeaderButton(
                            icon = Icons.Filled.FavoriteBorder,
                            onClick = onFavoriteClick
                        )
                        HeaderButton(
                            icon = Icons.Filled.Share,
                            onClick = onShareClick
                        )
                    }
                }
            }

            // Content Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-80).dp)
                    .padding(horizontal = Padding.padding16Dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Title Section
                TitleSection(event = event, sportColor = sportColor)

                // Host Card
                HostCard(
                    hostName = event.creatorName.ifBlank { "Host" },
                    onChatClick = onHostChatClick
                )

                // Details Grid
                DetailsGrid(event = event, sportUi = sportUi)

                // Meeting Point Section
                MeetingPointSection(
                    venueName = event.venue.name,
                    venueAddress = event.venue.address,
                    meetingPoint = event.venue.meetingPoint,
                    onOpenMapClick = onOpenMapClick
                )

                // Squad Lineup Section
                SquadLineupSection(
                    participants = participants,
                    currentPlayers = event.currentPlayers,
                    playerLimit = event.playerLimit,
                    spotsLeft = spotsLeft,
                    isFull = isFull
                )

                // Description/Notes Section
                if (event.description.isNotBlank()) {
                    NotesSection(description = event.description)
                }

                // Bottom spacing for fixed button
                Spacer(modifier = Modifier.height(120.dp))
            }
        }

        // Fixed Bottom Bar
        BottomActionBar(
            spotsLeft = spotsLeft,
            isFull = isFull,
            isCurrentUserHost = isCurrentUserHost,
            isCurrentUserParticipant = isCurrentUserParticipant,
            isJoining = isJoining,
            isLeaving = isLeaving,
            onJoinClick = onJoinClick,
            onLeaveClick = onLeaveClick,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun HeaderButton(
    icon: ImageVector,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.2f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
private fun TitleSection(
    event: Event,
    sportColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Badges row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Match Details badge
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        RoundedCornerShape(50)
                    )
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                LabelSmall(
                    text = "MATCH DETAILS",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            // Public badge
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Public,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(14.dp)
                )
                LabelSmall(
                    text = "Public",
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }

        // Match name
        TitleLarge(
            text = event.matchName,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.extendedColors.textDark
        )

        // Tags row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Player format tag
            if (event.playerLimit > 0) {
                InfoTag(
                    icon = Icons.Filled.Groups,
                    text = "${event.playerLimit}v${event.playerLimit}",
                    iconColor = MaterialTheme.extendedColors.emerald
                )
            }

            // Skill level tag
            if (event.skillLevel.isNotBlank()) {
                InfoTag(
                    icon = Icons.Filled.Star,
                    text = event.skillLevel,
                    iconColor = MaterialTheme.extendedColors.orange
                )
            }

            // Duration tag (if time is set)
            if (event.time.isNotBlank()) {
                InfoTag(
                    icon = Icons.Filled.Schedule,
                    text = event.time,
                    iconColor = MaterialTheme.extendedColors.purple
                )
            }
        }
    }
}

@Composable
private fun InfoTag(
    icon: ImageVector,
    text: String,
    iconColor: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(16.dp)
        )
        LabelSmall(
            text = text,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.extendedColors.textDark
        )
    }
}

@Composable
private fun HostCard(
    hostName: String,
    onChatClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host avatar placeholder
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                LabelLarge(
                    text = hostName.firstOrNull()?.uppercase() ?: "H",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Column {
                LabelSmall(
                    text = "HOST",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontWeight = FontWeight.Bold
                )
                BodyMedium(
                    text = hostName,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textDark
                )
            }
        }

        // Chat button placeholder
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(1.dp, MaterialTheme.extendedColors.outline, CircleShape)
                .clickable { onChatClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Share,
                contentDescription = "Chat",
                tint = MaterialTheme.extendedColors.textDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun DetailsGrid(
    event: Event,
    sportUi: com.harsh.playspot.ui.core.SportUi?
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Date card
            DetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.CalendarMonth,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                iconColor = MaterialTheme.colorScheme.primary,
                label = "Date",
                value = event.date
            )

            // Time card
            DetailCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.AccessTime,
                iconBgColor = MaterialTheme.extendedColors.emerald.copy(alpha = 0.1f),
                iconColor = MaterialTheme.extendedColors.emerald,
                label = "Time",
                value = event.time.ifBlank { "TBD" }
            )
        }

        // Sport type card (full width)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background((sportUi?.color ?: MaterialTheme.colorScheme.primary).copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                sportUi?.let {
                    Icon(
                        imageVector = it.icon,
                        contentDescription = null,
                        tint = it.color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Column {
                LabelSmall(
                    text = "Sport Type",
                    color = MaterialTheme.colorScheme.outlineVariant
                )
                BodyMedium(
                    text = event.sportType,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.extendedColors.textDark
                )
            }
        }
    }
}

@Composable
private fun DetailCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconBgColor: Color,
    iconColor: Color,
    label: String,
    value: String
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Column {
            LabelSmall(
                text = label,
                color = MaterialTheme.colorScheme.outlineVariant
            )
            BodyMedium(
                text = value,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )
        }
    }
}

@Composable
private fun MeetingPointSection(
    venueName: String,
    venueAddress: String,
    meetingPoint: String,
    onOpenMapClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        TitleMedium(
            text = "Meeting Point",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(16.dp))
        ) {
            // Map placeholder with gradient
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                            )
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.Center)
                )
            }

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 100f
                        )
                    )
            )

            // Venue info at bottom
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                TitleMedium(
                    text = venueName.ifBlank { "Venue" },
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                if (meetingPoint.isNotBlank()) {
                    BodySmall(
                        text = meetingPoint,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                } else if (venueAddress.isNotBlank()) {
                    BodySmall(
                        text = venueAddress,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Open Map button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onOpenMapClick() }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    LabelSmall(
                        text = "Open Map",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                    Icon(
                        imageVector = Icons.Filled.NorthEast,
                        contentDescription = null,
                        tint = MaterialTheme.extendedColors.textDark,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SquadLineupSection(
    participants: List<ParticipantUi>,
    currentPlayers: Int,
    playerLimit: Int,
    spotsLeft: Int,
    isFull: Boolean
) {
    val fallbackColors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.extendedColors.orange,
        MaterialTheme.extendedColors.emerald,
        MaterialTheme.extendedColors.purple
    )

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleMedium(
                text = "Squad Lineup",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )
            if (!isFull && spotsLeft <= 3) {
                LabelSmall(
                    text = "Filling fast",
                    color = MaterialTheme.extendedColors.emerald,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Progress bar
        if (playerLimit > 0) {
            val progress = currentPlayers.toFloat() / playerLimit.toFloat()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(MaterialTheme.extendedColors.outline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.extendedColors.emerald
                                )
                            )
                        )
                )
            }
        }

        // Player count card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(1.dp, MaterialTheme.extendedColors.outline, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar stack with actual participant images
            Row {
                val displayParticipants = participants.take(4)
                displayParticipants.forEachIndexed { index, participant ->
                    Box(
                        modifier = Modifier
                            .offset(x = (-8 * index).dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(fallbackColors[index % 4])
                            .border(2.dp, MaterialTheme.extendedColors.widgetBg, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (participant.avatarUrl.isNotBlank()) {
                            AsyncImage(
                                model = participant.avatarUrl,
                                contentDescription = participant.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Fallback to initial
                            LabelSmall(
                                text = participant.name.firstOrNull()?.uppercase() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                if (currentPlayers > 4) {
                    Box(
                        modifier = Modifier
                            .offset(x = (-32).dp)
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.extendedColors.widgetBg)
                            .border(1.dp, MaterialTheme.extendedColors.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        LabelSmall(
                            text = "+${currentPlayers - 4}",
                            color = MaterialTheme.colorScheme.outlineVariant,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Row(verticalAlignment = Alignment.Bottom) {
                    TitleMedium(
                        text = "$currentPlayers",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                    if (playerLimit > 0) {
                        BodySmall(
                            text = "/$playerLimit",
                            color = MaterialTheme.colorScheme.outlineVariant
                        )
                    }
                }
                LabelSmall(
                    text = "PLAYERS",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NotesSection(description: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.05f))
            .border(
                1.dp,
                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp)
    ) {
        BodyMedium(
            text = description,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun BottomActionBar(
    spotsLeft: Int,
    isFull: Boolean,
    isCurrentUserHost: Boolean,
    isCurrentUserParticipant: Boolean,
    isJoining: Boolean,
    isLeaving: Boolean,
    onJoinClick: () -> Unit,
    onLeaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
            .padding(top = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.extendedColors.outline,
                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Status column
            Column(modifier = Modifier.width(100.dp)) {
                LabelSmall(
                    text = "STATUS",
                    color = MaterialTheme.colorScheme.outlineVariant,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                when {
                    isFull -> BodyMedium(
                        text = "Full",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.orange
                    )
                    spotsLeft > 0 -> BodyMedium(
                        text = "$spotsLeft Spots Left",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.emerald
                    )
                    else -> BodyMedium(
                        text = "Open",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Action button
            val buttonBrush = when {
                isCurrentUserHost -> Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.outlineVariant,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                )
                isCurrentUserParticipant -> Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.extendedColors.red,
                        MaterialTheme.extendedColors.red
                    )
                )
                isFull -> Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.outlineVariant,
                        MaterialTheme.colorScheme.outlineVariant
                    )
                )
                else -> Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.extendedColors.purple
                    )
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(buttonBrush)
                    .clickable(
                        enabled = !isJoining && !isLeaving && !isCurrentUserHost && (!isFull || isCurrentUserParticipant)
                    ) {
                        if (isCurrentUserParticipant) {
                            onLeaveClick()
                        } else {
                            onJoinClick()
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                when {
                    isJoining || isLeaving -> {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    isCurrentUserHost -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LabelLarge(
                                text = "You're Hosting",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    isCurrentUserParticipant -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LabelLarge(
                                text = "Leave Match",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                    isFull -> {
                        LabelLarge(
                            text = "Match Full",
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    else -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            LabelLarge(
                                text = "Join Match",
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

