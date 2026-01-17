package com.harsh.playspot.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.extendedColors
import org.jetbrains.compose.resources.painterResource
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.skateboarder

/**
 * iOS implementation - Uses a placeholder image
 * Native MapKit integration via UIKitView has compatibility issues with Compose 1.9.x
 * TODO: Implement native MapKit when UIKitView is stable
 */
@Composable
actual fun GoogleMapView(
    modifier: Modifier,
    initialLocation: MapLocation,
    markers: List<MapLocation>,
    onMapClick: (MapLocation) -> Unit,
    onMarkerClick: (MapLocation) -> Unit
) {
    Box(modifier = modifier) {
        // Map placeholder image with overlay
        Image(
            painter = painterResource(Res.drawable.skateboarder),
            contentDescription = "Map placeholder",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.3f
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            MaterialTheme.colorScheme.background.copy(alpha = 0.8f)
                        )
                    )
                )
        )

        // Center location indicator
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
            
            if (initialLocation.name.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.extendedColors.widgetBg)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    BodyMedium(
                        text = initialLocation.name,
                        color = MaterialTheme.extendedColors.textDark,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Info text at bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 60.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.extendedColors.widgetBg.copy(alpha = 0.9f))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            LabelSmall(
                text = "Use search to find venues",
                color = MaterialTheme.colorScheme.outlineVariant,
                fontWeight = FontWeight.Medium
            )
        }

        // Map controls (visual only on iOS)
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MapControlButton(
                icon = Icons.Filled.MyLocation,
                onClick = { /* No-op on iOS placeholder */ }
            )
            MapControlButton(
                icon = Icons.Filled.Layers,
                onClick = { /* No-op on iOS placeholder */ }
            )
        }
    }
}

@Composable
private fun MapControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
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
