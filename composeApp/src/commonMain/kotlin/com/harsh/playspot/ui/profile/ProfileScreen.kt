package com.harsh.playspot.ui.profile

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material.icons.filled.SportsTennis
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
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
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.HeadlineMedium
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.OutlinedPrimaryButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleLarge
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.clickWithFeedback
import com.harsh.playspot.ui.core.extendedColors
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.cropped_circle_image
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
    onBackPressed: () -> Unit
) {
    ProfileScreen(onBackPressed = onBackPressed)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackPressed: () -> Unit = {},
    onShareClicked: () -> Unit = {},
    onEditProfileClicked: () -> Unit = {},
    onViewHistoryClicked: () -> Unit = {},
    onNotificationsClicked: () -> Unit = {},
    onPrivacyClicked: () -> Unit = {},
    onHelpClicked: () -> Unit = {},
    onLogoutClicked: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
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
                    },
                    title = {
                        Text(
                            text = stringResource(Res.string.profile_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    },
                    actions = {
                        Icon(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clickWithFeedback(HapticFeedbackType.Confirm) {
                                    onShareClicked()
                                },
                            imageVector = Icons.Filled.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.primary
                        )
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
                    name = "Alex Johnson",
                    username = "@alex_j",
                    location = "San Francisco, CA",
                    onEditProfileClicked = onEditProfileClicked
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Stats Section
                StatsSection(onViewHistoryClicked = onViewHistoryClicked)

                Spacer(modifier = Modifier.height(24.dp))

                // My Sports Section
                MySportsSection()

                Spacer(modifier = Modifier.height(24.dp))

                // Settings Section
                SettingsSection(
                    onNotificationsClicked = onNotificationsClicked,
                    onPrivacyClicked = onPrivacyClicked,
                    onHelpClicked = onHelpClicked,
                    onLogoutClicked = onLogoutClicked
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
    onEditProfileClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Padding.padding16Dp)
            .padding(top = Padding.padding24Dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Avatar with Glow
        Box(contentAlignment = Alignment.Center) {
            // Gradient glow effect
            Box(
                modifier = Modifier
                    .size(136.dp)
                    .blur(8.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                Color(0xFF60A5FA) // blue-400
                            )
                        ),
                        shape = CircleShape
                    )
            )

            // Profile image
            Box(
                modifier = Modifier
                    .size(128.dp)
                    .clip(CircleShape)
                    .border(
                        width = 4.dp,
                        color = MaterialTheme.extendedColors.widgetBg,
                        shape = CircleShape
                    )
            ) {
                Image(
                    painter = painterResource(Res.drawable.cropped_circle_image),
                    contentDescription = "Profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            // Verified badge
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.extendedColors.widgetBg)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Verified,
                    contentDescription = "Verified",
                    tint = MaterialTheme.extendedColors.green,
                    modifier = Modifier.size(16.dp)
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
private fun MySportsSection() {
    // Sample sports data
    val sports = listOf(
        Triple(Icons.Filled.SportsBasketball, Color(0xFFF97316), "Basketball"),
        Triple(Icons.Filled.SportsTennis, Color(0xFFEAB308), "Tennis"),
        Triple(Icons.Filled.Pool, Color(0xFF3B82F6), "Swimming")
    )

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
        val chunkedSports = sports.chunked(2)
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            chunkedSports.forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { (icon, tint, label) ->
                        SportChip(
                            modifier = Modifier.weight(1f),
                            icon = icon,
                            iconTint = tint,
                            label = label
                        )
                    }
                    // If odd number of items, add the AddSportChip or spacer
                    if (rowItems.size == 1) {
                        AddSportChip(modifier = Modifier.weight(1f))
                    }
                }
            }
            // Add button row if even number of sports
            if (sports.size % 2 == 0) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    AddSportChip(modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
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
    onLogoutClicked: () -> Unit
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
        Button(
            onClick = onLogoutClicked,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent,
                contentColor = MaterialTheme.extendedColors.red
            ),
            contentPadding = PaddingValues(16.dp)
        ) {
            LabelLarge(
                text = stringResource(Res.string.profile_logout),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.extendedColors.red
            )
        }
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

@Preview
@Composable
fun ProfileScreenPreview() {
    ProfileScreen()
}
