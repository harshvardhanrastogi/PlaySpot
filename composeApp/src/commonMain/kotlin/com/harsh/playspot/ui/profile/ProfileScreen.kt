package com.harsh.playspot.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.DangerButton
import com.harsh.playspot.ui.core.HeadlineMedium
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.OutlinedPrimaryButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.SportUi
import com.harsh.playspot.ui.core.TitleLarge
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.clickWithFeedback
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_bio
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_play_time_title
import playspot.composeapp.generated.resources.pref_set_up_finish_profile_skill_level
import playspot.composeapp.generated.resources.profile_edit
import playspot.composeapp.generated.resources.profile_logout
import playspot.composeapp.generated.resources.profile_my_sports
import playspot.composeapp.generated.resources.profile_settings
import playspot.composeapp.generated.resources.profile_settings_help
import playspot.composeapp.generated.resources.profile_settings_notifications
import playspot.composeapp.generated.resources.profile_settings_privacy
import playspot.composeapp.generated.resources.profile_stats_matches
import playspot.composeapp.generated.resources.profile_stats_title
import playspot.composeapp.generated.resources.profile_stats_view_history
import playspot.composeapp.generated.resources.profile_stats_win_rate
import playspot.composeapp.generated.resources.profile_stats_wins
import playspot.composeapp.generated.resources.profile_title

@Composable
fun ProfileScreenRoute(
    onBackPressed: () -> Unit,
    onLogoutSuccess: () -> Unit = {},
    onAddSportClicked: () -> Unit = {},
    onEditPictureClicked: () -> Unit = {},
    viewModel: ProfileViewModel = viewModel { ProfileViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current

    // Refresh profile when screen resumes (e.g., returning from EditSports)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is ProfileEvent.LogoutSuccess -> onLogoutSuccess()
                is ProfileEvent.LogoutError -> snackbarHostState.showSnackbar(event.message)
                is ProfileEvent.SaveSuccess -> snackbarHostState.showSnackbar("Profile updated!")
                is ProfileEvent.SaveError -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    ProfileScreen(
        onBackPressed = {
            if (uiState.isEditing) {
                viewModel.cancelEditing()
            } else {
                onBackPressed()
            }
        },
        onEditProfileClicked = { viewModel.startEditing() },
        onEditPictureClicked = onEditPictureClicked,
        onLogoutClicked = { viewModel.logout() },
        onAddSportClicked = onAddSportClicked,
        onBioChange = viewModel::onBioChange,
        onSkillLevelChange = viewModel::onSkillLevelChange,
        onPlayTimeToggle = viewModel::onPlayTimeToggle,
        onSaveClicked = viewModel::saveChanges,
        onCancelEditClicked = viewModel::cancelEditing,
        isLoggingOut = uiState.isLoggingOut,
        isSaving = uiState.isSaving,
        isEditing = uiState.isEditing,
        hasUnsavedChanges = uiState.hasUnsavedChanges,
        snackbarHostState = snackbarHostState,
        name = uiState.name,
        username = uiState.username,
        location = uiState.location,
        bio = uiState.bio,
        editedBio = uiState.editedBio,
        skillLevel = uiState.skillLevel,
        editedSkillLevel = uiState.editedSkillLevel,
        playTimes = uiState.playTimes,
        editedPlayTimes = uiState.editedPlayTimes,
        preferredSports = uiState.preferredSports,
        profilePictureUrl = uiState.profilePictureUrl
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackPressed: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onEditProfileClicked: () -> Unit = {},
    onEditPictureClicked: () -> Unit = {},
    onViewHistoryClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onPrivacyClicked: () -> Unit = {},
    onHelpClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {},
    onAddSportClicked: () -> Unit = {},
    onBioChange: (String) -> Unit = {},
    onSkillLevelChange: (String) -> Unit = {},
    onPlayTimeToggle: (String) -> Unit = {},
    onSaveClicked: () -> Unit = {},
    onCancelEditClicked: () -> Unit = {},
    isLoggingOut: Boolean = false,
    isSaving: Boolean = false,
    isEditing: Boolean = false,
    hasUnsavedChanges: Boolean = false,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    name: String = "Alex Johnson",
    username: String = "@alex_j",
    location: String = "San Francisco, CA",
    bio: String = "",
    editedBio: String = "",
    skillLevel: String = "",
    editedSkillLevel: String = "",
    playTimes: List<String> = emptyList(),
    editedPlayTimes: List<String> = emptyList(),
    preferredSports: List<String> = emptyList(),
    profilePictureUrl: String? = null
) {
    val scrollState = rememberScrollState()

    AppTheme {
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        if (isEditing) {
                            // Cancel button when editing
                            LabelLarge(
                                text = "Cancel",
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickWithFeedback(HapticFeedbackType.Confirm) {
                                        onCancelEditClicked()
                                    }
                            )
                        } else {
                            Icon(
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickWithFeedback(HapticFeedbackType.Confirm) {
                                        onBackPressed()
                                    },
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = MaterialTheme.extendedColors.textDark
                            )
                        }
                    },
                    title = {
                        Text(
                            text = if (isEditing) "Edit Profile" else stringResource(Res.string.profile_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    actions = {
                        if (isEditing && hasUnsavedChanges) {
                            // Save button when editing with changes
                            if (isSaving) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .padding(horizontal = 16.dp)
                                        .size(24.dp),
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            } else {
                                LabelLarge(
                                    text = "Save",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .clickWithFeedback(HapticFeedbackType.Confirm) {
                                            onSaveClicked()
                                        }
                                )
                            }
                        } else if (isEditing) {
                            // Empty space to balance the toolbar when editing without changes
                            Spacer(modifier = Modifier.minimumInteractiveComponentSize())
                        } else {
                            // Edit button when not editing
                            LabelLarge(
                                text = "Edit",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .clickWithFeedback(HapticFeedbackType.Confirm) {
                                        onEditProfileClicked()
                                    }
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                // Profile Header
                ProfileHeader(
                    name = name.ifEmpty { "Alex Johnson" },
                    username = username.ifEmpty { "@alex_j" },
                    location = location.ifEmpty { "San Francisco, CA" },
                    profilePictureUrl = profilePictureUrl,
                    onEditProfileClicked = onEditProfileClicked,
                    onEditPictureClicked = onEditPictureClicked
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                StatsSection(onViewHistoryClicked = onViewHistoryClicked)

                Spacer(modifier = Modifier.height(24.dp))

                // Bio Section
                BioSection(
                    bio = if (isEditing) editedBio else bio,
                    isEditing = isEditing,
                    onBioChange = onBioChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Skill Level Section
                SkillLevelSection(
                    skillLevel = if (isEditing) editedSkillLevel else skillLevel,
                    isEditing = isEditing,
                    onSkillLevelChange = onSkillLevelChange
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Play Time Section
                PlayTimeSection(
                    playTimes = if (isEditing) editedPlayTimes else playTimes,
                    isEditing = isEditing,
                    onPlayTimeToggle = onPlayTimeToggle
                )

                Spacer(modifier = Modifier.height(24.dp))

                // My Sports Section
                MySportsSection(
                    preferredSports = preferredSports,
                    onAddSportClicked = onAddSportClicked
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                SettingsSection(
                    onNotificationsClicked = onNotificationsClicked,
                    onPrivacyClicked = onPrivacyClicked,
                    onHelpClicked = onHelpClicked,
                    onLogoutClicked = onLogoutClicked,
                    isLoggingOut = isLoggingOut
                )

                Spacer(modifier = Modifier.height(100.dp)) // Bottom nav space
            }
        }
    }
}

@Composable
private fun ProfileHeader(
    name: String,
    username: String,
    location: String,
    profilePictureUrl: String? = null,
    onEditProfileClicked: () -> Unit,
    onEditPictureClicked: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.padding16Dp)
            .padding(top = Padding.padding24Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with Edit Badge
        Box(contentAlignment = Alignment.Center) {
            // Profile image
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.extendedColors.widgetBg,
                        shape = CircleShape
                    )
                    .clickWithFeedback(HapticFeedbackType.LongPress) {
                        onEditPictureClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                if (!profilePictureUrl.isNullOrBlank()) {
                    // Load profile picture from ImageKit
                    AsyncImage(
                        model = profilePictureUrl,
                        contentDescription = "Profile picture",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Fallback to placeholder icon
                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile picture",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }

            // Camera/Edit badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                    .clickWithFeedback(HapticFeedbackType.Confirm) {
                        onEditPictureClicked()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.PhotoCamera,
                    contentDescription = "Edit profile picture",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Name
        HeadlineMedium(text = name, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(4.dp))

        // Username & Location
        BodyMedium(
            text = "$username • $location",
            color = MaterialTheme.colorScheme.outlineVariant,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Edit Profile Button
        OutlinedPrimaryButton(
            modifier = Modifier.width(200.dp),
            label = stringResource(Res.string.profile_edit),
            onClick = onEditProfileClicked
        )
    }
}

@Composable
private fun StatsSection(onViewHistoryClicked: () -> Unit) {
    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Padding.padding4Dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TitleMedium(
                text = stringResource(Res.string.profile_stats_title),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )
            LabelSmall(
                text = stringResource(Res.string.profile_stats_view_history),
                color = MaterialTheme.colorScheme.outlineVariant,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickWithFeedback(HapticFeedbackType.LongPress) {
                    onViewHistoryClicked()
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.SportsScore,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                value = "24",
                label = stringResource(Res.string.profile_stats_matches),
                valueColor = MaterialTheme.extendedColors.textDark
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Filled.EmojiEvents,
                iconTint = MaterialTheme.extendedColors.amber,
                iconBgColor = MaterialTheme.extendedColors.amberContainer,
                value = "18",
                label = stringResource(Res.string.profile_stats_wins),
                valueColor = MaterialTheme.extendedColors.textDark
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.AutoMirrored.Filled.TrendingUp,
                iconTint = MaterialTheme.extendedColors.emerald,
                iconBgColor = MaterialTheme.extendedColors.emeraldContainer,
                value = "75%",
                label = stringResource(Res.string.profile_stats_win_rate),
                valueColor = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    value: String,
    label: String,
    valueColor: Color
) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(Padding.padding16Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Value
        TitleLarge(text = value, color = valueColor, fontWeight = FontWeight.SemiBold)

        Spacer(modifier = Modifier.height(2.dp))

        // Label
        LabelSmall(
            text = label,
            color = MaterialTheme.colorScheme.outlineVariant,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun MySportsSection(
    preferredSports: List<String>,
    onAddSportClicked: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        TitleMedium(
            modifier = Modifier.padding(horizontal = Padding.padding4Dp),
            text = stringResource(Res.string.profile_my_sports),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Sports Grid (2 columns)
        val chunkedSports = preferredSports.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            chunkedSports.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { sport ->
                        val sportUi = getSportUi(sport)!!
                        SportChip(
                            modifier = Modifier.weight(1f),
                            icon = sportUi.icon,
                            iconTint = sportUi.color,
                            label = sportUi.name
                        )
                    }
                    // If odd number of items, add the AddSportChip or spacer
                    if (rowItems.size == 1) {
                        AddSportChip(
                            modifier = Modifier.weight(1f),
                            onClick = onAddSportClicked
                        )
                    }
                }
            }
            // Add button row if even number of sports
            if (preferredSports.size % 2 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AddSportChip(
                        modifier = Modifier.weight(1f),
                        onClick = onAddSportClicked
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun getSportUi(sport: String): SportUi? {
    return getSportsMap()[sport]
}


@Composable
private fun SportChip(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    iconTint: Color,
    label: String
) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(horizontal = Padding.padding16Dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(20.dp)
        )
        LabelLarge(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.extendedColors.textDark
        )
    }
}

@Composable
private fun PlayTimeChip(
    modifier: Modifier = Modifier,
    label: String
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(horizontal = Padding.padding16Dp, vertical = 14.dp)
    ) {
        LabelLarge(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.extendedColors.textDark
        )
    }
}

@Composable
private fun AddSportChip(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(12.dp)

    Row(
        modifier = modifier
            .clip(shape)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .clickWithFeedback(HapticFeedbackType.LongPress) { onClick() }
            .padding(horizontal = Padding.padding16Dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = Icons.Filled.Add,
            contentDescription = "Add sport",
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(20.dp)
        )
        LabelLarge(
            text = "Add Sport",
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
private fun SettingsSection(
    onNotificationsClicked: () -> Unit,
    onPrivacyClicked: () -> Unit,
    onHelpClicked: () -> Unit,
    onLogoutClicked: () -> Unit,
    isLoggingOut: Boolean = false
) {
    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        TitleMedium(
            modifier = Modifier.padding(horizontal = Padding.padding4Dp),
            text = stringResource(Res.string.profile_settings),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Settings List
        val shape = RoundedCornerShape(12.dp)

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
        ) {
            SettingsItem(
                icon = Icons.Filled.Notifications,
                iconTint = MaterialTheme.extendedColors.indigo,
                iconBgColor = MaterialTheme.extendedColors.indigoContainer,
                label = stringResource(Res.string.profile_settings_notifications),
                onClick = onNotificationsClicked
            )

            HorizontalDivider(
                color = MaterialTheme.extendedColors.outline,
                thickness = 1.dp
            )

            SettingsItem(
                icon = Icons.Filled.Lock,
                iconTint = MaterialTheme.colorScheme.primary,
                iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                label = stringResource(Res.string.profile_settings_privacy),
                onClick = onPrivacyClicked
            )

            HorizontalDivider(
                color = MaterialTheme.extendedColors.outline,
                thickness = 1.dp
            )

            SettingsItem(
                icon = Icons.AutoMirrored.Filled.Help,
                iconTint = MaterialTheme.extendedColors.teal,
                iconBgColor = MaterialTheme.extendedColors.tealContainer,
                label = stringResource(Res.string.profile_settings_help),
                onClick = onHelpClicked
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logout Button
        DangerButton(
            label = stringResource(Res.string.profile_logout),
            isLoading = isLoggingOut,
            onClick = onLogoutClicked
        )
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    iconTint: Color,
    iconBgColor: Color,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickWithFeedback(HapticFeedbackType.LongPress) { onClick() }
            .padding(Padding.padding16Dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Icon background
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(iconBgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(18.dp)
                )
            }

            LabelLarge(
                text = label,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.extendedColors.textDark
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.size(16.dp)
        )
    }
}

@Composable
private fun BioSection(
    bio: String,
    isEditing: Boolean = false,
    onBioChange: (String) -> Unit = {}
) {
    val shape = RoundedCornerShape(12.dp)

    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        TitleMedium(
            modifier = Modifier.padding(horizontal = Padding.padding4Dp),
            text = stringResource(Res.string.pref_set_up_finish_profile_bio),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Bio Text or TextField when editing
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = if (isEditing) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.outline,
                    shape = shape
                )
                .padding(Padding.padding16Dp)
        ) {
            if (isEditing) {
                BasicTextField(
                    value = bio,
                    onValueChange = onBioChange,
                    modifier = Modifier.fillMaxWidth().height(80.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.extendedColors.textDark
                    ),
                    decorationBox = { innerTextField ->
                        Box {
                            if (bio.isEmpty()) {
                                BodyMedium(
                                    text = "Tell others about yourself...",
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            } else if (bio.isNotEmpty()) {
                BodyMedium(
                    text = bio,
                    color = MaterialTheme.extendedColors.textDark
                )
            } else {
                BodyMedium(
                    text = "Tell others about yourself...",
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun SkillLevelSection(
    skillLevel: String,
    isEditing: Boolean = false,
    onSkillLevelChange: (String) -> Unit = {}
) {
    val skillLevelStates = getSkillLevelStates()
    val selectedSkillLevelState = skillLevelStates.find { it.skillLevel.name == skillLevel }
    val shape = RoundedCornerShape(12.dp)

    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        TitleMedium(
            modifier = Modifier.padding(horizontal = Padding.padding4Dp),
            text = stringResource(Res.string.pref_set_up_finish_profile_skill_level),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isEditing) {
            // Editing mode - show all skill levels as selectable options
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                skillLevelStates.forEach { skillLevelState ->
                    val isSelected = skillLevelState.skillLevel.name == skillLevel
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(shape)
                            .background(MaterialTheme.extendedColors.widgetBg)
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.outline,
                                shape = shape
                            )
                            .clickWithFeedback(HapticFeedbackType.LongPress) {
                                onSkillLevelChange(skillLevelState.skillLevel.name)
                            }
                            .padding(Padding.padding16Dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Icon background
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(skillLevelState.skillLevel.iconBgColor()),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = skillLevelState.skillLevel.iconRes,
                                contentDescription = null,
                                tint = skillLevelState.skillLevel.iconTint(),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            LabelLarge(
                                text = stringResource(skillLevelState.title),
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.extendedColors.textDark
                            )
                            LabelSmall(
                                text = stringResource(skillLevelState.desc),
                                color = MaterialTheme.colorScheme.outlineVariant,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Filled.Verified,
                                contentDescription = "Selected",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        } else if (selectedSkillLevelState != null) {
            // Display mode - show selected skill level
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon background
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(selectedSkillLevelState.skillLevel.iconBgColor()),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = selectedSkillLevelState.skillLevel.iconRes,
                        contentDescription = null,
                        tint = selectedSkillLevelState.skillLevel.iconTint(),
                        modifier = Modifier.size(20.dp)
                    )
                }

                Column {
                    LabelLarge(
                        text = stringResource(selectedSkillLevelState.title),
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                    LabelSmall(
                        text = stringResource(selectedSkillLevelState.desc),
                        color = MaterialTheme.colorScheme.outlineVariant,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = shape
                    )
                    .padding(Padding.padding16Dp)
            ) {
                BodyMedium(
                    text = "Select your skill level...",
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun PlayTimeSection(
    playTimes: List<String>,
    isEditing: Boolean = false,
    onPlayTimeToggle: (String) -> Unit = {}
) {
    val shape = RoundedCornerShape(12.dp)
    val allPlayTimes = listOf("Morning", "Afternoon", "Evening", "Night", "Weekends")

    Column(modifier = Modifier.padding(horizontal = Padding.padding16Dp)) {
        // Section Header
        TitleMedium(
            modifier = Modifier.padding(horizontal = Padding.padding4Dp),
            text = stringResource(Res.string.pref_set_up_finish_profile_play_time_title),
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.extendedColors.textDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (isEditing) {
            // Editing mode - show all play times as toggleable chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                allPlayTimes.forEach { playTime ->
                    val isSelected = playTimes.contains(playTime)
                    EditablePlayTimeChip(
                        label = playTime,
                        isSelected = isSelected,
                        onClick = { onPlayTimeToggle(playTime) }
                    )
                }
            }
        } else if (playTimes.isNotEmpty()) {
            // Display mode - show selected play times
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                playTimes.forEach { playTime ->
                    PlayTimeChip(label = playTime)
                }
            }
        } else {
            // Empty State
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(shape)
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = shape
                    )
                    .padding(Padding.padding16Dp)
            ) {
                BodyMedium(
                    text = "When do you usually play?",
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun EditablePlayTimeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = Modifier
            .clip(shape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                else MaterialTheme.extendedColors.widgetBg
            )
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .clickWithFeedback(HapticFeedbackType.LongPress) { onClick() }
            .padding(horizontal = Padding.padding16Dp, vertical = 14.dp)
    ) {
        LabelLarge(
            text = label,
            fontWeight = FontWeight.SemiBold,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.extendedColors.textDark
        )
    }
}

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}

@Preview
@Composable
fun ChipPreview() {
    val sport = getSportsMap()["Yoga"]
    sport ?: return
    AppTheme {
        SportChip(icon = sport.icon, iconTint = sport.color, label = sport.name)
    }
}
