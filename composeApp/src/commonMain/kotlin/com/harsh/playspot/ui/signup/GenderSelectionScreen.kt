package com.harsh.playspot.ui.signup

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Female
import androidx.compose.material.icons.filled.Male
import androidx.compose.material.icons.filled.Transgender
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.TitleLarge
import com.harsh.playspot.ui.core.extendedColors
import kotlinx.coroutines.launch

data class GenderOption(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun GenderSelectionScreenRoute(
    onCancelOnboarding: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val firestoreRepository = remember { FirestoreRepository.instance }
    val authRepository = remember { AuthRepository.getInstance() }
    var selectedGender by remember { mutableStateOf<String?>(null) }
    var isSaving by remember { mutableStateOf(false) }
    var showCancelDialog by remember { mutableStateOf(false) }
    
    // Cancel Onboarding Confirmation Dialog
    if (showCancelDialog) {
        AlertDialog(
            onDismissRequest = { showCancelDialog = false },
            title = {
                Text(
                    text = "Cancel Setup?",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "You can complete your profile setup later from the Profile screen."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCancelDialog = false
                        onCancelOnboarding()
                    }
                ) {
                    Text(
                        text = "Yes, Skip",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showCancelDialog = false }) {
                    Text("Continue Setup")
                }
            }
        )
    }
    
    GenderSelectionScreen(
        selectedGender = selectedGender,
        isSaving = isSaving,
        onGenderSelected = { gender ->
            selectedGender = gender
        },
        onBackPressed = { showCancelDialog = true },
        onContinue = {
            if (selectedGender != null) {
                isSaving = true
                scope.launch {
                    // Save gender to user profile
                    val uid = authRepository.currentUser?.uid
                    if (uid != null) {
                        firestoreRepository.updateDocument(
                            collection = CollectionNames.USER_PROFILE,
                            documentId = uid,
                            updates = mapOf("gender" to selectedGender)
                        )
                    }
                    isSaving = false
                    onContinue()
                }
            }
        },
        onSkip = onSkip
    )
}

@Composable
private fun GenderSelectionScreen(
    selectedGender: String?,
    isSaving: Boolean = false,
    onGenderSelected: (String) -> Unit,
    onBackPressed: () -> Unit,
    onContinue: () -> Unit,
    onSkip: () -> Unit
) {
    val genderOptions = listOf(
        GenderOption("male", "Male", Icons.Filled.Male),
        GenderOption("female", "Female", Icons.Filled.Female),
        GenderOption("other", "Other / Prefer not to say", Icons.Filled.Transgender)
    )
    
    AppTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top Navigation
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    LabelSmall(
                        text = "Step 2 of 5",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        fontWeight = FontWeight.Medium
                    )
                    // Spacer to balance the row
                    Box(modifier = Modifier.size(48.dp))
                }
                
                // Progress Bar
                LinearProgressIndicator(
                    progress = { 0.4f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.extendedColors.outline,
                    strokeCap = StrokeCap.Round
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Header
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    TitleLarge(
                        text = "Tell us about yourself",
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    BodyMedium(
                        text = "What is your gender?",
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Gender Options
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    genderOptions.forEach { option ->
                        GenderOptionCard(
                            option = option,
                            isSelected = selectedGender == option.id,
                            onClick = { onGenderSelected(option.id) }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Footer Actions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Button(
                        onClick = onContinue,
                        enabled = selectedGender != null && !isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        )
                    ) {
                        Text(
                            text = if (isSaving) "Saving..." else "Continue",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    TextButton(
                        onClick = onSkip,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Skip for now",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GenderOptionCard(
    option: GenderOption,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.extendedColors.outline
    }
    
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
    } else {
        Color.Transparent
    }
    
    val iconColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.extendedColors.widgetBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = option.icon,
                contentDescription = option.label,
                tint = iconColor,
                modifier = Modifier.size(28.dp)
            )
        }
        
        // Label
        Text(
            text = option.label,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )
        
        // Check mark
        if (isSelected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Selected",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
