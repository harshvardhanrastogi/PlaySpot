package com.harsh.playspot.ui.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.HeadlineLarge
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.ProfileAction
import com.harsh.playspot.ui.core.clickWithFeedback
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.isIOS
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.cropped_circle_image
import playspot.composeapp.generated.resources.profile_picture_choose_gallery
import playspot.composeapp.generated.resources.profile_picture_desc
import playspot.composeapp.generated.resources.profile_picture_save_continue
import playspot.composeapp.generated.resources.profile_picture_skip
import playspot.composeapp.generated.resources.profile_picture_take_photo
import playspot.composeapp.generated.resources.profile_picture_title

@Composable
fun AddProfilePictureScreenRoute(
    onBackPressed: () -> Unit,
    onSkipClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    viewModel: AddProfilePictureViewModel = viewModel { AddProfilePictureViewModel() }
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    // Gallery picker - works on both platforms
    val galleryLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                viewModel.onImageSelected(bytes)
            }
        }
    )

    AddProfilePictureScreen(
        onBackPressed = onBackPressed,
        onSkipClicked = onSkipClicked,
        onGalleryClicked = { galleryLauncher.launch() },
        // Camera is disabled on iOS due to UIKitView compatibility issues
        onCameraClicked = { 
            if (!isIOS) {
                // TODO: Re-enable camera when Peekaboo is compatible with Compose 1.9.x
                // For now, fall back to gallery on iOS
                galleryLauncher.launch()
            } else {
                galleryLauncher.launch()
            }
        },
        onSaveClicked = onSaveClicked,
        selectedImageBytes = uiState.selectedImageBytes,
        showCameraOption = !isIOS // Hide camera option on iOS
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddProfilePictureScreen(
    onBackPressed: () -> Unit = {},
    onSkipClicked: () -> Unit = {},
    onGalleryClicked: () -> Unit = {},
    onCameraClicked: () -> Unit = {},
    onSaveClicked: () -> Unit = {},
    selectedImageBytes: ByteArray? = null,
    showCameraOption: Boolean = true
) {
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
                            contentDescription = null,
                            tint = MaterialTheme.extendedColors.textDark
                        )
                    },
                    title = {},
                    actions = {
                        LabelLarge(
                            modifier = Modifier
                                .minimumInteractiveComponentSize()
                                .clickWithFeedback(HapticFeedbackType.Confirm) {
                                    onSkipClicked()
                                },
                            text = stringResource(Res.string.profile_picture_skip),
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors()
                        .copy(containerColor = Color.Transparent)
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = Padding.padding24Dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(48.dp))

                // Profile Image Placeholder with Camera Badge
                ProfileImagePlaceholder(
                    modifier = Modifier,
                    selectedImageBytes = selectedImageBytes,
                    onClick = onGalleryClicked
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Title
                HeadlineLarge(
                    modifier = Modifier.fillMaxWidth(),
                    text = stringResource(Res.string.profile_picture_title),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                BodyLarge(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Padding.padding16Dp),
                    text = stringResource(Res.string.profile_picture_desc),
                    color = MaterialTheme.colorScheme.outlineVariant,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Upload Options
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileAction(
                        icon = Icons.Filled.Image,
                        iconTint = MaterialTheme.colorScheme.primary,
                        iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        text = stringResource(Res.string.profile_picture_choose_gallery),
                        desc = "",
                        trailing = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(16.dp)
                            )
                        },
                        onActionClick = onGalleryClicked
                    )

                    // Camera option - hidden on iOS due to UIKitView compatibility
                    if (showCameraOption) {
                        ProfileAction(
                            icon = Icons.Filled.PhotoCamera,
                            iconTint = MaterialTheme.colorScheme.primary,
                            iconBgColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            text = stringResource(Res.string.profile_picture_take_photo),
                            desc = "",
                            trailing = {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.outline,
                                    modifier = Modifier.size(16.dp)
                                )
                            },
                            onActionClick = onCameraClicked
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Save & Continue Button
                LargeButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Padding.padding24Dp),
                    label = stringResource(Res.string.profile_picture_save_continue),
                    enabled = selectedImageBytes != null,
                    onClick = onSaveClicked
                )
            }
        }
    }
}

@Composable
private fun ProfileImagePlaceholder(
    modifier: Modifier = Modifier,
    selectedImageBytes: ByteArray? = null,
    onClick: () -> Unit = {}
) {
    Box(
        modifier = modifier
            .clickWithFeedback(HapticFeedbackType.LongPress) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Main circular image with ring
        Box(
            modifier = Modifier
                .size(160.dp)
                .shadow(elevation = 16.dp, shape = CircleShape)
                .clip(CircleShape)
                .border(
                    width = 4.dp,
                    color = MaterialTheme.extendedColors.widgetBg,
                    shape = CircleShape
                )
        ) {
            if (selectedImageBytes != null) {
                Image(
                    bitmap = selectedImageBytes.toImageBitmap(),
                    contentDescription = "Selected profile picture",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Image(
                    painter = painterResource(Res.drawable.cropped_circle_image),
                    contentDescription = "Profile placeholder",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Camera badge
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = (-8).dp, y = (-8).dp)
                .size(44.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .border(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.background,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.PhotoCamera,
                contentDescription = "Add photo",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Preview
@Composable
fun AddProfilePictureScreenPreview() {
    AddProfilePictureScreen()
}
