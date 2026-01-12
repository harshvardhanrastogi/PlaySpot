package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val location: String = "",
    val locationAddress: String = "",
    val meetingPoint: String = "",
    val description: String = "",
    val skillLevel: SkillLevel = SkillLevel.Beginner,
    val isLoading: Boolean = false,
    val matchNameError: String? = null,
    val sportTypeError: String? = null,
    val dateError: String? = null
) {
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
                location == other.location &&
                locationAddress == other.locationAddress &&
                meetingPoint == other.meetingPoint &&
                description == other.description &&
                skillLevel == other.skillLevel &&
                isLoading == other.isLoading &&
                matchNameError == other.matchNameError &&
                sportTypeError == other.sportTypeError &&
                dateError == other.dateError
    }

    override fun hashCode(): Int {
        var result = coverImageBytes?.contentHashCode() ?: 0
        result = 31 * result + matchName.hashCode()
        result = 31 * result + sportType.hashCode()
        result = 31 * result + date.hashCode()
        result = 31 * result + time.hashCode()
        result = 31 * result + playerLimit.hashCode()
        result = 31 * result + location.hashCode()
        result = 31 * result + locationAddress.hashCode()
        result = 31 * result + meetingPoint.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + skillLevel.hashCode()
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (matchNameError?.hashCode() ?: 0)
        result = 31 * result + (sportTypeError?.hashCode() ?: 0)
        result = 31 * result + (dateError?.hashCode() ?: 0)
        return result
    }
}

sealed class CreateEventEvent {
    data object CreateSuccess : CreateEventEvent()
    data class CreateError(val message: String) : CreateEventEvent()
    data object SaveDraftSuccess : CreateEventEvent()
}

class CreateEventViewModel : ViewModel() {

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

    fun onLocationChange(location: String, address: String = "") {
        _uiState.update { it.copy(location = location, locationAddress = address) }
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
            _events.emit(CreateEventEvent.SaveDraftSuccess)
        }
    }

    fun createMatch() {
        val currentState = _uiState.value

        // Validate inputs
        var hasError = false
        var matchNameError: String? = null
        var sportTypeError: String? = null
        var dateError: String? = null

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

        if (hasError) {
            _uiState.update {
                it.copy(
                    matchNameError = matchNameError,
                    sportTypeError = sportTypeError,
                    dateError = dateError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // TODO: Save event to Firestore
            // For now, just emit success
            _uiState.update { it.copy(isLoading = false) }
            _events.emit(CreateEventEvent.CreateSuccess)
        }
    }
}
