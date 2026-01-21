package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.EventStatus
import com.harsh.playspot.dao.Venue
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
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
    val locationError: String? = null
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
                locationError == other.locationError
    }

    override fun hashCode(): Int {
        var result = coverImageBytes?.contentHashCode() ?: 0
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
        return result
    }
}

sealed class CreateEventEvent {
    data object CreateSuccess : CreateEventEvent()
    data class CreateError(val message: String) : CreateEventEvent()
    data object SaveDraftSuccess : CreateEventEvent()
}

class CreateEventViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateEventUiState())
    val uiState: StateFlow<CreateEventUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateEventEvent>()
    val events: SharedFlow<CreateEventEvent> = _events.asSharedFlow()

    fun onCoverImageSelected(bytes: ByteArray) {
        _uiState.update { it.copy(coverImageBytes = bytes) }
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
                val event = createEventFromState(currentState, currentUser?.uid ?: "", EventStatus.DRAFT)
                
                firestoreRepository.addDocument(CollectionNames.EVENTS, event)
                    .onSuccess {
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
                
                val event = createEventFromState(currentState, currentUser.uid, EventStatus.UPCOMING)
                
                firestoreRepository.addDocument(CollectionNames.EVENTS, event)
                    .onSuccess { eventId ->
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
    
    private fun createEventFromState(
        state: CreateEventUiState,
        creatorId: String,
        status: String
    ): Event {
        val currentTimeMillis = System.currentTimeMillis()
        
        return Event(
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
            participants = listOf(creatorId), // Creator joins automatically
            status = status,
            createdAt = currentTimeMillis,
            updatedAt = currentTimeMillis
        )
    }
}
