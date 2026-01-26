package com.harsh.playspot.ui.events

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.harsh.playspot.SharedEvent
import com.harsh.playspot.SharedEventHandler
import com.harsh.playspot.ui.core.AppTheme
import com.harsh.playspot.ui.core.BodyLarge
import com.harsh.playspot.ui.core.BodyMedium
import com.harsh.playspot.ui.core.BodySmall
import com.harsh.playspot.ui.core.LabelLarge
import com.harsh.playspot.ui.core.LabelSmall
import com.harsh.playspot.ui.core.LargeButton
import com.harsh.playspot.ui.core.Padding
import com.harsh.playspot.ui.core.TitleMedium
import com.harsh.playspot.ui.core.extendedColors
import com.harsh.playspot.ui.core.getSportsMap
import com.harsh.playspot.ui.profile.SkillLevel
import com.harsh.playspot.ui.profile.getSkillLevelStates
import com.preat.peekaboo.image.picker.SelectionMode
import com.preat.peekaboo.image.picker.rememberImagePickerLauncher
import com.preat.peekaboo.image.picker.toImageBitmap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import playspot.composeapp.generated.resources.Res
import playspot.composeapp.generated.resources.skateboarder

@Composable
fun CreateEventScreenRoute(
    onBackPressed: () -> Unit,
    onEventCreated: () -> Unit,
    eventId: String? = null,
    viewModel: CreateEventViewModel = viewModel(key = eventId ?: "create") {
        CreateEventViewModel(eventIdToEdit = eventId)
    }
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var showLocationSelection by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Image picker launcher
    val imagePickerLauncher = rememberImagePickerLauncher(
        selectionMode = SelectionMode.Single,
        scope = scope,
        onResult = { byteArrays ->
            byteArrays.firstOrNull()?.let { bytes ->
                viewModel.onCoverImageSelected(bytes)
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { event ->
            when (event) {
                is CreateEventEvent.CreateSuccess -> {
                    onEventCreated()
                    SharedEventHandler.sendEvent(SharedEvent.EventCreated)
                }

                is CreateEventEvent.UpdateSuccess -> {
                    onEventCreated()
                    SharedEventHandler.sendEvent(SharedEvent.EventCreated)
                }

                is CreateEventEvent.DeleteSuccess -> {
                    onEventCreated()
                    SharedEventHandler.sendEvent(SharedEvent.EventCreated)
                }

                is CreateEventEvent.CreateError -> snackbarHostState.showSnackbar(event.message)
                is CreateEventEvent.SaveDraftSuccess -> snackbarHostState.showSnackbar("Draft saved")
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteConfirmation) {
        DeleteConfirmationDialog(
            onConfirm = {
                showDeleteConfirmation = false
                viewModel.deleteEvent()
            },
            onDismiss = { showDeleteConfirmation = false }
        )
    }

    if (showLocationSelection) {
        LocationSelectionScreenWithDetails(
            onBackPressed = { showLocationSelection = false },
            onLocationSelected = { name, address, placeId, lat, lng ->
                viewModel.onLocationChange(
                    name = name,
                    address = address,
                    placeId = placeId,
                    latitude = lat,
                    longitude = lng
                )
                showLocationSelection = false
            }
        )
    } else {
        CreateEventScreen(
            uiState = uiState,
            snackbarHostState = snackbarHostState,
            onCancelClick = onBackPressed,
            onSaveDraftClick = viewModel::saveDraft,
            onCoverPhotoClick = { imagePickerLauncher.launch() },
            onMatchNameChange = viewModel::onMatchNameChange,
            onSportTypeChange = viewModel::onSportTypeChange,
            onDateChange = viewModel::onDateChange,
            onTimeChange = viewModel::onTimeChange,
            onPlayerLimitChange = viewModel::onPlayerLimitChange,
            onLocationClick = { showLocationSelection = true },
            onMeetingPointChange = viewModel::onMeetingPointChange,
            onDescriptionChange = viewModel::onDescriptionChange,
            onSkillLevelChange = viewModel::onSkillLevelChange,
            onCreateMatchClick = if (uiState.isEditMode) viewModel::updateEvent else viewModel::createMatch,
            onDeleteClick = { showDeleteConfirmation = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateEventScreen(
    uiState: CreateEventUiState,
    snackbarHostState: SnackbarHostState,
    onCancelClick: () -> Unit,
    onSaveDraftClick: () -> Unit,
    onCoverPhotoClick: () -> Unit,
    onMatchNameChange: (String) -> Unit,
    onSportTypeChange: (String) -> Unit,
    onDateChange: (String) -> Unit,
    onTimeChange: (String) -> Unit,
    onPlayerLimitChange: (String) -> Unit,
    onLocationClick: () -> Unit,
    onMeetingPointChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onSkillLevelChange: (SkillLevel) -> Unit,
    onCreateMatchClick: () -> Unit,
    onDeleteClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    AppTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        TitleMedium(
                            text = if (uiState.isEditMode) "Edit Match" else "Create Match",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.extendedColors.textDark
                        )
                    },
                    navigationIcon = {
                        Text(
                            text = "Cancel",
                            modifier = Modifier
                                .clickable { onCancelClick() }
                                .padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    actions = {
                        if (uiState.isEditMode) {
                            Text(
                                text = if (uiState.isDeleting) "Deleting..." else "Delete",
                                modifier = Modifier
                                    .clickable(enabled = !uiState.isDeleting) { onDeleteClick() }
                                    .padding(horizontal = 16.dp),
                                color = Color(0xFFEF4444),
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Text(
                                text = "Save Draft",
                                modifier = Modifier
                                    .clickable { onSaveDraftClick() }
                                    .padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.extendedColors.widgetBg
                    )
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.extendedColors.widgetBg)
                        .padding(horizontal = 16.dp, vertical = 16.dp)
                        .padding(bottom = 24.dp)
                ) {
                    LargeButton(
                        modifier = Modifier.fillMaxWidth(),
                        label = if (uiState.isEditMode) "Update Match" else "Create Match",
                        isLoading = uiState.isLoading,
                        onClick = onCreateMatchClick
                    )
                }
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
                    .padding(horizontal = Padding.padding16Dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Cover Photo Section
                CoverPhotoSection(
                    coverImageBytes = uiState.coverImageBytes,
                    coverImageUrl = uiState.coverImageUrl,
                    onCoverPhotoClick = onCoverPhotoClick
                )

                // Match Name Input
                InputSection(
                    label = "MATCH NAME",
                    isPrimaryLabel = true
                ) {
                    StyledTextField(
                        value = uiState.matchName,
                        onValueChange = onMatchNameChange,
                        placeholder = "e.g. Saturday Morning 5v5",
                        isLarge = true,
                        isError = uiState.matchNameError != null,
                        maxCount = 30
                    )
                    if (uiState.matchNameError != null) {
                        BodySmall(
                            text = uiState.matchNameError,
                            color = MaterialTheme.extendedColors.red,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // Sport Type Dropdown
                InputSection(
                    label = "SPORT TYPE",
                    isPrimaryLabel = true
                ) {
                    SportTypeDropdown(
                        selectedSport = uiState.sportType,
                        onSportSelected = onSportTypeChange,
                        isError = uiState.sportTypeError != null
                    )
                    if (uiState.sportTypeError != null) {
                        BodySmall(
                            text = uiState.sportTypeError,
                            color = MaterialTheme.extendedColors.red,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                HorizontalDivider(color = MaterialTheme.extendedColors.outline)

                // Game Details Section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.EditCalendar,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    TitleMedium(
                        text = "Game Details",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                }

                // Date and Time Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    InputSection(
                        label = "Date",
                        modifier = Modifier.weight(1f)
                    ) {
                        DatePickerField(
                            value = uiState.date,
                            onDateSelected = onDateChange,
                            isError = uiState.dateError != null
                        )
                    }

                    InputSection(
                        label = "Time",
                        modifier = Modifier.weight(1f)
                    ) {
                        TimePickerField(
                            value = uiState.time,
                            onTimeSelected = onTimeChange
                        )
                    }
                }

                // Player Limit
                InputSection(label = "Player Limit") {
                    PlayerLimitInput(
                        value = uiState.playerLimit,
                        onValueChange = onPlayerLimitChange
                    )
                }

                // Location Section
                InputSection(label = "Location & Meeting Point") {
                    LocationCard(
                        location = uiState.location.ifEmpty { "Tap to select location" },
                        address = uiState.locationAddress.ifEmpty { "Choose a venue for your match" },
                        meetingPoint = uiState.meetingPoint,
                        onLocationClick = onLocationClick,
                        onMeetingPointChange = onMeetingPointChange
                    )
                }

                // Description
                InputSection(label = "Description") {
                    DescriptionTextField(
                        value = uiState.description,
                        onValueChange = onDescriptionChange
                    )
                }

                HorizontalDivider(color = MaterialTheme.extendedColors.outline)

                // Skill Level
                InputSection(
                    label = "SKILL LEVEL",
                    isPrimaryLabel = true
                ) {
                    SkillLevelSelector(
                        selectedLevel = uiState.skillLevel,
                        onLevelSelected = onSkillLevelChange
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }

    }
}

@Composable
private fun CoverPhotoSection(
    coverImageBytes: ByteArray?,
    coverImageUrl: String,
    onCoverPhotoClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)
    val hasImage = coverImageBytes != null || coverImageUrl.isNotBlank()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .clickable { onCoverPhotoClick() },
        contentAlignment = Alignment.Center
    ) {
        when {
            // Display newly selected image from bytes (takes priority)
            coverImageBytes != null -> {
                Image(
                    bitmap = coverImageBytes.toImageBitmap(),
                    contentDescription = "Cover photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Display existing image from URL
            coverImageUrl.isNotBlank() -> {
                AsyncImage(
                    model = coverImageUrl,
                    contentDescription = "Cover photo",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
            // Empty state - show placeholder
            else -> {
                Image(
                    painter = painterResource(Res.drawable.skateboarder),
                    contentDescription = "Cover placeholder",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.25f
                )

                // Overlay with add photo prompt
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddAPhoto,
                            contentDescription = "Add photo",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    BodyMedium(
                        text = "Add Cover Photo",
                        color = MaterialTheme.colorScheme.outlineVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Edit button overlay (show when any image exists)
        if (hasImage) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Change photo",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun InputSection(
    label: String,
    modifier: Modifier = Modifier,
    isPrimaryLabel: Boolean = false,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontSize = if (isPrimaryLabel) 11.sp else 12.sp,
            fontWeight = if (isPrimaryLabel) FontWeight.Bold else FontWeight.SemiBold,
            color = if (isPrimaryLabel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
            letterSpacing = if (isPrimaryLabel) 1.sp else 0.sp,
            modifier = Modifier.padding(start = 4.dp)
        )
        content()
    }
}

@Composable
private fun StyledTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isLarge: Boolean = false,
    isError: Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Text,
    maxCount : Int
) {
    val shape = RoundedCornerShape(16.dp)
    val count = maxCount - value.count()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = if (isError) MaterialTheme.extendedColors.red else MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = {
                if (count > 0 || it.count() < value.count()) {
                    onValueChange(it)
                } else {
                    onValueChange(it.take(maxCount))
                }
            },
            singleLine = true,
            textStyle = TextStyle(
                fontSize = if (isLarge) 18.sp else 16.sp,
                fontWeight = if (isLarge) FontWeight.SemiBold else FontWeight.Normal,
                color = MaterialTheme.extendedColors.textDark
            ),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            keyboardActions = KeyboardActions(
                onDone = {
                    // This hides the keyboard when Done is pressed
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            modifier = Modifier.fillMaxWidth(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    BodyLarge(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                if (value.isNotEmpty()) {
                    LabelSmall(
                        text = "$count", textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    value: String,
    onDateSelected: (String) -> Unit,
    isError: Boolean = false
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val shape = RoundedCornerShape(16.dp)

    // Date picker dialog
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val formattedDate = formatDateFromMillis(millis)
                            onDateSelected(formattedDate)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = if (isError) MaterialTheme.extendedColors.red else MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .clickable { showDatePicker = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.ifEmpty { "Select date" },
            fontSize = 16.sp,
            color = if (value.isEmpty())
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            else
                MaterialTheme.extendedColors.textDark
        )
        Icon(
            imageVector = Icons.Filled.CalendarToday,
            contentDescription = "Select date",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    value: String,
    onTimeSelected: (String) -> Unit
) {
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState()
    val shape = RoundedCornerShape(16.dp)

    // Time picker dialog
    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            onConfirm = {
                val hour = timePickerState.hour
                val minute = timePickerState.minute
                val formattedTime =
                    "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
                onTimeSelected(formattedTime)
                showTimePicker = false
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }

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
            .clickable { showTimePicker = true }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = value.ifEmpty { "Select time" },
            fontSize = 16.sp,
            color = if (value.isEmpty())
                MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
            else
                MaterialTheme.extendedColors.textDark
        )
        Icon(
            imageVector = Icons.Filled.AccessTime,
            contentDescription = "Select time",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerDialog(
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismissRequest) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Select time",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            )
            content()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onDismissRequest) {
                    Text("Cancel")
                }
                TextButton(onClick = onConfirm) {
                    Text("OK")
                }
            }
        }
    }
}

@Composable
private fun SportTypeDropdown(
    selectedSport: String,
    onSportSelected: (String) -> Unit,
    isError: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    val sportsMap = getSportsMap()
    val sports = sportsMap.keys.toList()

    val shape = RoundedCornerShape(16.dp)

    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(MaterialTheme.extendedColors.widgetBg)
                .border(
                    width = 1.dp,
                    color = if (isError) MaterialTheme.extendedColors.red else MaterialTheme.extendedColors.outline,
                    shape = shape
                )
                .clickable { expanded = true }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = selectedSport.ifEmpty { "Select Sport..." },
                fontSize = 16.sp,
                color = if (selectedSport.isEmpty())
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                else
                    MaterialTheme.extendedColors.textDark
            )
            Icon(
                imageVector = Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outlineVariant
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(MaterialTheme.extendedColors.widgetBg)
        ) {
            sports.forEach { sport ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = sport,
                            color = MaterialTheme.extendedColors.textDark
                        )
                    },
                    onClick = {
                        onSportSelected(sport)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun PlayerLimitInput(
    value: String,
    onValueChange: (String) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

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
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.Group,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }

        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.extendedColors.textDark
            ),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Max players allowed",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun LocationCard(
    location: String,
    address: String,
    meetingPoint: String,
    onLocationClick: () -> Unit,
    onMeetingPointChange: (String) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

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
        // Map placeholder with gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        )
                    )
                )
                .clickable { onLocationClick() }
        ) {
            // Edit button
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f))
                    .clickable { onLocationClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Edit location",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    TitleMedium(
                        text = location,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.extendedColors.textDark
                    )
                    BodySmall(
                        text = address,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                }
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(top = 12.dp),
                color = MaterialTheme.extendedColors.outline
            )

            Row(
                modifier = Modifier.padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Flag,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outlineVariant,
                    modifier = Modifier.size(20.dp)
                )
                BasicTextField(
                    value = meetingPoint,
                    onValueChange = onMeetingPointChange,
                    textStyle = TextStyle(
                        fontSize = 14.sp,
                        color = MaterialTheme.extendedColors.textDark
                    ),
                    modifier = Modifier.weight(1f),
                    decorationBox = { innerTextField ->
                        if (meetingPoint.isEmpty()) {
                            Text(
                                text = "Meeting point (e.g. Entrance Gate 2)",
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}

@Composable
private fun DescriptionTextField(
    value: String,
    onValueChange: (String) -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(shape)
            .background(MaterialTheme.extendedColors.widgetBg)
            .border(
                width = 1.dp,
                color = MaterialTheme.extendedColors.outline,
                shape = shape
            )
            .padding(16.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(
                fontSize = 16.sp,
                color = MaterialTheme.extendedColors.textDark
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    defaultKeyboardAction(ImeAction.Done)
                }
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxSize(),
            decorationBox = { innerTextField ->
                if (value.isEmpty()) {
                    Text(
                        text = "Add details about the game, required gear, or skill expectations...",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.7f)
                    )
                }
                innerTextField()
            }
        )
    }
}

@Composable
private fun SkillLevelSelector(
    selectedLevel: SkillLevel,
    onLevelSelected: (SkillLevel) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        getSkillLevelStates().forEach { state ->
            val isSelected = selectedLevel == state.skillLevel
            val shape = RoundedCornerShape(50)

            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(
                        if (isSelected)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.extendedColors.widgetBg
                    )
                    .border(
                        width = if (isSelected) 0.dp else 1.dp,
                        color = MaterialTheme.extendedColors.outline,
                        shape = shape
                    )
                    .clickable { onLevelSelected(state.skillLevel) }
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                LabelLarge(
                    text = stringResource(state.title),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .clip(RoundedCornerShape(28.dp))
                .background(MaterialTheme.colorScheme.surface)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TitleMedium(
                text = "Delete Event",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.extendedColors.textDark
            )

            BodyMedium(
                text = "Are you sure you want to delete this event? This action cannot be undone.",
                color = MaterialTheme.colorScheme.outlineVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.extendedColors.outline)
                        .clickable { onDismiss() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LabelLarge(
                        text = "Cancel",
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.extendedColors.textDark
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFEF4444))
                        .clickable { onConfirm() }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LabelLarge(
                        text = "Delete",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}

private fun formatDateFromMillis(millis: Long): String {
    if (millis == 0L) return ""

    val months =
        listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

    // Convert millis to an Instant
    val instant = Instant.fromEpochMilliseconds(millis)

    // Convert Instant to LocalDateTime using the system timezone
    val dateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())

    val day = dateTime.dayOfMonth
    val month = months[dateTime.monthNumber - 1] // monthNumber is 1-12
    val year = dateTime.year

    return "$day $month $year"
}

@Preview
@Composable
fun CreateEventScreenPreview() {
    CreateEventScreen(
        uiState = CreateEventUiState(),
        snackbarHostState = SnackbarHostState(),
        onCancelClick = {},
        onSaveDraftClick = {},
        onCoverPhotoClick = {},
        onMatchNameChange = {},
        onSportTypeChange = {},
        onDateChange = {},
        onTimeChange = {},
        onPlayerLimitChange = {},
        onLocationClick = {},
        onMeetingPointChange = {},
        onDescriptionChange = {},
        onSkillLevelChange = {},
        onCreateMatchClick = {},
        onDeleteClick = {}
    )
}


