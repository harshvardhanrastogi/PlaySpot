package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.currentTimeMillis
import com.harsh.playspot.generateUniqueId
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.EventStatus
import com.harsh.playspot.dao.Participant
import com.harsh.playspot.dao.UserEvent
import com.harsh.playspot.dao.Venue
import com.harsh.playspot.dao.generateUserEventId
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.data.imagekit.ImageKitRepository
import com.harsh.playspot.ui.profile.SkillLevel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CreateEventUiState(
    val coverImageBytes: ByteArray? = null,
    val coverImageUrl: String = "", // URL of uploaded/existing cover image
    val isUploadingImage: Boolean = false, // Image upload in progress
    val matchName: String = "",
    val sportType: String = "",
    val date: String = "",
    val time: String = "",
    val playerLimit: String = "",
    // Venue details
    val venueName: String = "",
    val venueAddress: String = "",
    val venuePlaceId: String = "",
    val venueLatitude: Double? = null,
    val venueLongitude: Double? = null,
    val meetingPoint: String = "",
    val description: String = "",
    val skillLevel: SkillLevel = SkillLevel.Beginner,
    val isLoading: Boolean = false,
    val matchNameError: String? = null,
    val sportTypeError: String? = null,
    val dateError: String? = null,
    val locationError: String? = null,
    // Edit mode fields
    val isEditMode: Boolean = false,
    val eventId: String = "",
    val isDeleting: Boolean = false
) {
    // For backward compatibility
    val location: String get() = venueName
    val locationAddress: String get() = venueAddress

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as CreateEventUiState
        if (coverImageBytes != null) {
            if (other.coverImageBytes == null) return false
            if (!coverImageBytes.contentEquals(other.coverImageBytes)) return false
        } else if (other.coverImageBytes != null) return false
        return matchName == other.matchName &&
                coverImageUrl == other.coverImageUrl &&
                isUploadingImage == other.isUploadingImage &&
                sportType == other.sportType &&
                date == other.date &&
                time == other.time &&
                playerLimit == other.playerLimit &&
                venueName == other.venueName &&
                venueAddress == other.venueAddress &&
                venuePlaceId == other.venuePlaceId &&
                venueLatitude == other.venueLatitude &&
                venueLongitude == other.venueLongitude &&
                meetingPoint == other.meetingPoint &&
                description == other.description &&
                skillLevel == other.skillLevel &&
                isLoading == other.isLoading &&
                matchNameError == other.matchNameError &&
                sportTypeError == other.sportTypeError &&
                dateError == other.dateError &&
                locationError == other.locationError &&
                isEditMode == other.isEditMode &&
                eventId == other.eventId &&
                isDeleting == other.isDeleting
    }

    override fun hashCode(): Int {
        var result = coverImageBytes?.contentHashCode() ?: 0
        result = 31 * result + coverImageUrl.hashCode()
        result = 31 * result + isUploadingImage.hashCode()
        result = 31 * result + matchName.hashCode()
        result = 31 * result + sportType.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + playerLimit.hashCode()
        result = 31 * result + venueName.hashCode()
        result = 31 * result + venueAddress.hashCode()
        result = 31 * result + venuePlaceId.hashCode()
        result = 31 * result + (venueLatitude?.hashCode() ?: 0)
        result = 31 * result + (venueLongitude?.hashCode() ?: 0)
        result = 31 * result + meetingPoint.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + skillLevel.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (matchNameError?.hashCode() ?: 0)
        result = 31 * result + (sportTypeError?.hashCode() ?: 0)
        result = 31 * result + (dateError?.hashCode() ?: 0)
        result = 31 * result + (locationError?.hashCode() ?: 0)
        result = 31 * result + isEditMode.hashCode()
        result = 31 * result + eventId.hashCode()
        result = 31 * result + isDeleting.hashCode()
        return result
    }
}

sealed class CreateEventEvent {
    data object CreateSuccess : CreateEventEvent()
    data object UpdateSuccess : CreateEventEvent()
    data object DeleteSuccess : CreateEventEvent()
    data class CreateError(val message: String) : CreateEventEvent()
    data object SaveDraftSuccess : CreateEventEvent()
}

class CreateEventViewModel(
    private val eventIdToEdit: String? = null,
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateEventEvent>()
    val events: SharedFlow<CreateEventEvent> = _events.asSharedFlow()

    init {
        // Load event if editing
        if (!eventIdToEdit.isNullOrBlank()) {
            loadEvent(eventIdToEdit)
        }
    }

    /**
     * Load an existing event for editing
     */
    fun loadEvent(eventId: String) {
        if (eventId.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isEditMode = true, eventId = eventId) }

            firestoreRepository.getDocument<Event>(CollectionNames.EVENTS, eventId)
                .onSuccess { event ->
                    if (event != null) {
                        val skillLevel = try {
                            SkillLevel.valueOf(event.skillLevel)
                        } catch (e: Exception) {
                            SkillLevel.Beginner
                        }

                        // Get optimized image URL if cover image exists
                        val optimizedCoverUrl = if (event.coverImageUrl.isNotBlank()) {
                            imageKitRepository.getEventCoverUrl(event.coverImageUrl)
                        } else ""

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                matchName = event.matchName,
                                sportType = event.sportType,
                                date = event.date,
                                time = event.time,
                                playerLimit = if (event.playerLimit > 0) event.playerLimit.toString() else "",
                                venueName = event.venue.name,
                                venueAddress = event.venue.address,
                                venuePlaceId = event.venue.placeId,
                                venueLatitude = if (event.venue.latitude != 0.0) event.venue.latitude else null,
                                venueLongitude = if (event.venue.longitude != 0.0) event.venue.longitude else null,
                                meetingPoint = event.venue.meetingPoint,
                                description = event.description,
                                skillLevel = skillLevel,
                                coverImageUrl = optimizedCoverUrl
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.CreateError("Event not found"))
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to load event"))
                }
        }
    }

    fun onCoverImageSelected(bytes: ByteArray) {
        // Clear existing URL when new image is selected (will be uploaded on save)
        _uiState.update { it.copy(coverImageBytes = bytes, coverImageUrl = "") }
    }

    /**
     * Upload cover image to ImageKit
     * @param eventId The event ID for naming the image
     * @return URL of the uploaded image or null if upload fails or no image selected
     */
    private suspend fun uploadCoverImageIfNeeded(eventId: String): String? {
        val currentState = _uiState.value
        val imageBytes = currentState.coverImageBytes ?: return currentState.coverImageUrl.ifBlank { null }

        // If we have bytes but also have the same URL (image was downloaded), skip upload
        if (currentState.coverImageUrl.isNotBlank() && currentState.isEditMode) {
            // Check if user selected a new image by comparing - if bytes were just downloaded,
            // we shouldn't re-upload. We track this by clearing the URL when a new image is selected.
            return currentState.coverImageUrl
        }

        _uiState.update { it.copy(isUploadingImage = true) }

        return try {
            imageKitRepository.uploadEventCover(imageBytes, eventId)
                .onSuccess { response ->
                    _uiState.update { it.copy(isUploadingImage = false, coverImageUrl = response.url) }
                }
                .onFailure {
                    _uiState.update { it.copy(isUploadingImage = false) }
                }
                .getOrNull()?.url
        } catch (e: Exception) {
            _uiState.update { it.copy(isUploadingImage = false) }
            null
        }
    }

    fun onMatchNameChange(name: String) {
        _uiState.update { it.copy(matchName = name, matchNameError = null) }
    }

    fun onSportTypeChange(sport: String) {
        _uiState.update { it.copy(sportType = sport, sportTypeError = null) }
    }

    fun onDateChange(date: String) {
        _uiState.update { it.copy(date = date, dateError = null) }
    }

    fun onTimeChange(time: String) {
        _uiState.update { it.copy(time = time) }
    }

    fun onPlayerLimitChange(limit: String) {
        _uiState.update { it.copy(playerLimit = limit) }
    }

    /**
     * Update venue details with full location information
     */
    fun onLocationChange(
        name: String,
        address: String = "",
        placeId: String = "",
        latitude: Double? = null,
        longitude: Double? = null
    ) {
        _uiState.update {
            it.copy(
                venueName = name,
                venueAddress = address,
                venuePlaceId = placeId,
                venueLatitude = latitude,
                venueLongitude = longitude,
                locationError = null
            )
        }
    }

    fun onMeetingPointChange(meetingPoint: String) {
        _uiState.update { it.copy(meetingPoint = meetingPoint) }
    }

    fun onDescriptionChange(description: String) {
        _uiState.update { it.copy(description = description) }
    }

    fun onSkillLevelChange(skillLevel: SkillLevel) {
        _uiState.update { it.copy(skillLevel = skillLevel) }
    }

    fun saveDraft() {
        viewModelScope.launch {
            val currentState = _uiState.value
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentUser = authRepository.currentUser
                val creatorId = currentUser?.uid ?: ""
                val creatorName = currentUser?.displayName ?: ""
                val creatorProfileUrl = currentUser?.photoURL ?: ""

                // Generate event ID first for image naming
                val eventId = generateUniqueId()

                // Upload cover image if selected
                val coverImageUrl = uploadCoverImageIfNeeded(eventId) ?: ""

                val event = createEventFromState(
                    state = currentState.copy(coverImageUrl = coverImageUrl),
                    creatorId = creatorId,
                    creatorName = creatorName,
                    creatorProfileUrl = creatorProfileUrl,
                    status = EventStatus.DRAFT,
                    eventId = eventId
                )

                firestoreRepository.setDocument(CollectionNames.EVENTS, event.id, event)
                    .onSuccess {
                        // Add creator to user_events collection
                        addCreatorToUserEvents(event, creatorId)

                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.SaveDraftSuccess)
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to save draft"))
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to save draft"))
            }
        }
    }

    fun createMatch() {
        val currentState = _uiState.value

        // Validate inputs
        var hasError = false
        var matchNameError: String? = null
        var sportTypeError: String? = null
        var dateError: String? = null
        var locationError: String? = null

        if (currentState.matchName.isBlank()) {
            matchNameError = "Match name is required"
            hasError = true
        }

        if (currentState.sportType.isBlank()) {
            sportTypeError = "Please select a sport"
            hasError = true
        }

        if (currentState.date.isBlank()) {
            dateError = "Date is required"
            hasError = true
        }

        if (currentState.venueName.isBlank()) {
            locationError = "Please select a venue"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    matchNameError = matchNameError,
                    sportTypeError = sportTypeError,
                    dateError = dateError,
                    locationError = locationError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                val currentUser = authRepository.currentUser
                if (currentUser == null) {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(CreateEventEvent.CreateError("Please login to create an event"))
                    return@launch
                }

                // Get creator info from Firebase Auth
                val creatorName = currentUser.displayName ?: ""
                val creatorProfileUrl = currentUser.photoURL ?: ""

                // Generate event ID first for image naming
                val eventId = generateUniqueId()

                // Upload cover image if selected
                val coverImageUrl = uploadCoverImageIfNeeded(eventId) ?: ""

                val event = createEventFromState(
                    state = currentState.copy(coverImageUrl = coverImageUrl),
                    creatorId = currentUser.uid,
                    creatorName = creatorName,
                    creatorProfileUrl = creatorProfileUrl,
                    status = EventStatus.UPCOMING,
                    eventId = eventId
                )

                firestoreRepository.setDocument(CollectionNames.EVENTS, event.id, event)
                    .onSuccess {
                        // Add creator to user_events collection
                        addCreatorToUserEvents(event, currentUser.uid)

                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.CreateSuccess)
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to create event"))
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to create event"))
            }
        }
    }

    /**
     * Update an existing event
     */
    fun updateEvent() {
        val currentState = _uiState.value

        if (!currentState.isEditMode || currentState.eventId.isBlank()) {
            viewModelScope.launch {
                _events.emit(CreateEventEvent.CreateError("Cannot update: not in edit mode"))
            }
            return
        }

        // Validate inputs
        var hasError = false
        var matchNameError: String? = null
        var sportTypeError: String? = null
        var dateError: String? = null
        var locationError: String? = null

        if (currentState.matchName.isBlank()) {
            matchNameError = "Match name is required"
            hasError = true
        }

        if (currentState.sportType.isBlank()) {
            sportTypeError = "Please select a sport"
            hasError = true
        }

        if (currentState.date.isBlank()) {
            dateError = "Date is required"
            hasError = true
        }

        if (currentState.venueName.isBlank()) {
            locationError = "Please select a venue"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    matchNameError = matchNameError,
                    sportTypeError = sportTypeError,
                    dateError = dateError,
                    locationError = locationError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Upload cover image if a new image was selected
                val coverImageUrl = uploadCoverImageIfNeeded(currentState.eventId)

                val updates = mutableMapOf<String, Any>(
                    "matchName" to currentState.matchName.trim(),
                    "sportType" to currentState.sportType,
                    "date" to currentState.date,
                    "time" to currentState.time,
                    "playerLimit" to (currentState.playerLimit.toIntOrNull() ?: 0),
                    "description" to currentState.description.trim(),
                    "skillLevel" to currentState.skillLevel.name,
                    "venue" to mapOf(
                        "name" to currentState.venueName,
                        "address" to currentState.venueAddress,
                        "meetingPoint" to currentState.meetingPoint.trim(),
                        "latitude" to (currentState.venueLatitude ?: 0.0),
                        "longitude" to (currentState.venueLongitude ?: 0.0),
                        "placeId" to currentState.venuePlaceId
                    ),
                    "updatedAt" to currentTimeMillis()
                )

                // Add cover image URL if available
                if (coverImageUrl != null) {
                    updates["coverImageUrl"] = coverImageUrl
                }

                firestoreRepository.updateDocument(CollectionNames.EVENTS, currentState.eventId, updates)
                    .onSuccess {
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.UpdateSuccess)
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isLoading = false) }
                        _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to update event"))
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to update event"))
            }
        }
    }

    /**
     * Delete an event
     */
    fun deleteEvent() {
        val currentState = _uiState.value
        val currentUserId = authRepository.currentUser?.uid

        if (!currentState.isEditMode || currentState.eventId.isBlank()) {
            viewModelScope.launch {
                _events.emit(CreateEventEvent.CreateError("Cannot delete: not in edit mode"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }

            try {
                firestoreRepository.deleteDocument(CollectionNames.EVENTS, currentState.eventId)
                    .onSuccess {
                        // Also remove creator's user_events entry
                        if (currentUserId != null) {
                            val userEventId = generateUserEventId(currentUserId, currentState.eventId)
                            firestoreRepository.deleteDocument(CollectionNames.USER_EVENTS, userEventId)
                        }

                        _uiState.update { it.copy(isDeleting = false) }
                        _events.emit(CreateEventEvent.DeleteSuccess)
                    }
                    .onFailure { e ->
                        _uiState.update { it.copy(isDeleting = false) }
                        _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to delete event"))
                    }
            } catch (e: Exception) {
                _uiState.update { it.copy(isDeleting = false) }
                _events.emit(CreateEventEvent.CreateError(e.message ?: "Failed to delete event"))
            }
        }
    }

    /**
     * Add the creator to the user_events collection
     */
    private suspend fun addCreatorToUserEvents(event: Event, creatorId: String) {
        val userEventId = generateUserEventId(creatorId, event.id)
        val userEvent = UserEvent(
            id = userEventId,
            userId = creatorId,
            eventId = event.id,
            eventName = event.matchName,
            sportType = event.sportType,
            date = event.date,
            time = event.time,
            venueName = event.venue.name,
            coverImageUrl = event.coverImageUrl,
            isCreator = true,
            joinedAt = currentTimeMillis()
        )
        firestoreRepository.setDocument(
            CollectionNames.USER_EVENTS,
            userEventId,
            userEvent
        )
    }

    private fun createEventFromState(
        state: CreateEventUiState,
        creatorId: String,
        creatorName: String,
        creatorProfileUrl: String,
        status: String,
        eventId: String = generateUniqueId()
    ): Event {
        val currentTimeMillis = currentTimeMillis()

        // Create creator as first participant
        val creatorParticipant = Participant(
            id = creatorId,
            name = creatorName.trim(),
            profileUrl = creatorProfileUrl
        )

        return Event(
            id = eventId,
            creatorName = creatorName.trim(),
            creatorProfileUrl = creatorProfileUrl,
            matchName = state.matchName.trim(),
            sportType = state.sportType,
            date = state.date,
            time = state.time,
            playerLimit = state.playerLimit.toIntOrNull() ?: 0,
            currentPlayers = 1, // Creator is the first player
            description = state.description.trim(),
            skillLevel = state.skillLevel.name,
            venue = Venue(
                name = state.venueName,
                address = state.venueAddress,
                meetingPoint = state.meetingPoint.trim(),
                latitude = state.venueLatitude ?: 0.0,
                longitude = state.venueLongitude ?: 0.0,
                placeId = state.venuePlaceId
            ),
            creatorId = creatorId,
            participants = listOf(creatorParticipant), // Creator joins automatically
            status = status,
            createdAt = currentTimeMillis,
            updatedAt = currentTimeMillis,
            coverImageUrl = state.coverImageUrl
        )
    }
}
