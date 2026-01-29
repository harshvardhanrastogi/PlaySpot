package com.harsh.playspot.ui.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.TitleLarge
import com.harsh.playspot.util.LocationProvider
import kotlinx.coroutines.launch

@Composable
expect fun LocationPermissionScreenRoute(
    onContinue: () -> Unit,
    onSkip: () -> Unit
)

@Composable
fun LocationPermissionScreen(
    onEnableLocation: () -> Unit,
    onSkip: () -> Unit
) {
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Step indicator at top
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.End
            ) {
                LabelSmall(
                    text = "Step 3 of 4",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                    fontWeight = FontWeight.SemiBold
                )
            }
            
            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustration
                LocationIllustration()
                
                Spacer(modifier = Modifier.height(40.dp))
                
                // Title
                TitleLarge(
                    text = "Find Matches Nearby",
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Description
                BodyMedium(
                    text = "To show you the best games and sports communities in your area, we need to know your location. You can change this anytime in settings.",
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            
            // Bottom buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Enable Location button
                Button(
                    onClick = onEnableLocation,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(12.dp),
                            ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                        ),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Enable Location",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Not Now button
                TextButton(
                    onClick = onSkip,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(
                        text = "Not Now",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationIllustration() {
    val primaryColor = MaterialTheme.colorScheme.primary
    
    Box(
        modifier = Modifier
            .size(280.dp)
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        primaryColor.copy(alpha = 0.15f),
                        primaryColor.copy(alpha = 0.05f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Abstract map lines in background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        ) {
            // Decorative circles
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.TopStart)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.08f))
            )
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.06f))
            )
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.CenterEnd)
                    .clip(CircleShape)
                    .background(primaryColor.copy(alpha = 0.1f))
            )
        }
        
        // Center pin icon with shadow ring
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .shadow(
                        elevation = 16.dp,
                        shape = CircleShape,
                        ambientColor = primaryColor.copy(alpha = 0.4f),
                        spotColor = primaryColor.copy(alpha = 0.4f)
                    )
                    .clip(CircleShape)
                    .background(primaryColor),
                contentAlignment = Alignment.Center
            ) {
                // White ring effect
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .clip(CircleShape)
                        .background(Color.Transparent)
                ) {
                    Icon(
                        imageVector = Icons.Filled.LocationOn,
                        contentDescription = "Location",
                        tint = Color.White,
                        modifier = Modifier
                            .size(40.dp)
                            .align(Alignment.Center)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Decorative dots below pin
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(32.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(primaryColor.copy(alpha = 0.4f))
                )
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(primaryColor.copy(alpha = 0.4f))
                )
            }
        }
    }
}
