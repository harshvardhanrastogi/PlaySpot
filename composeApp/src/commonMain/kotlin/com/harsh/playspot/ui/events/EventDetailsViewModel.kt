package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.Participant
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.data.imagekit.ImageKitRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * UI representation of a participant with optimized avatar URL
 */
data class ParticipantUi(
    val id: String,
    val name: String,
    val avatarUrl: String
)

data class EventDetailsUiState(
    val isLoading: Boolean = true,
    val event: Event? = null,
    val coverImageUrl: String = "",
    val participants: List<ParticipantUi> = emptyList(),
    val isCurrentUserHost: Boolean = false,
    val isCurrentUserParticipant: Boolean = false,
    val spotsLeft: Int = 0,
    val isFull: Boolean = false,
    val isJoining: Boolean = false,
    val isLeaving: Boolean = false,
    val error: String? = null
)

sealed class EventDetailsEvent {
    data object JoinSuccess : EventDetailsEvent()
    data object LeaveSuccess : EventDetailsEvent()
    data class Error(val message: String) : EventDetailsEvent()
}

class EventDetailsViewModel(
    private val eventId: String,
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailsUiState())
    val uiState: StateFlow<EventDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<EventDetailsEvent>()
    val events: SharedFlow<EventDetailsEvent> = _events.asSharedFlow()

    private val currentUserId: String?
        get() = authRepository.currentUser?.uid

    init {
        loadEvent()
    }

    fun loadEvent() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            firestoreRepository.getDocument<Event>(CollectionNames.EVENTS, eventId)
                .onSuccess { event ->
                    if (event != null) {
                        val userId = currentUserId
                        val isHost = event.creatorId == userId
                        val isParticipant = event.participants.any { it.id == userId }
                        val spotsLeft = if (event.playerLimit > 0) {
                            maxOf(0, event.playerLimit - event.currentPlayers)
                        } else 0
                        val isFull = event.playerLimit > 0 && event.currentPlayers >= event.playerLimit

                        // Get optimized cover image URL
                        val coverUrl = if (event.coverImageUrl.isNotBlank()) {
                            imageKitRepository.getEventCoverUrl(event.coverImageUrl)
                        } else ""

                        // Create participant UI objects with optimized avatar URLs
                        val participantsUi = event.participants.map { participant ->
                            ParticipantUi(
                                id = participant.id,
                                name = participant.name,
                                avatarUrl = if (participant.profileUrl.isNotBlank()) {
                                    imageKitRepository.getAvatarUrl(participant.profileUrl)
                                } else ""
                            )
                        }

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                event = event,
                                coverImageUrl = coverUrl,
                                participants = participantsUi,
                                isCurrentUserHost = isHost,
                                isCurrentUserParticipant = isParticipant,
                                spotsLeft = spotsLeft,
                                isFull = isFull
                            )
                        }
                    } else {
                        _uiState.update {
                            it.copy(isLoading = false, error = "Event not found")
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load event")
                    }
                }
        }
    }

    fun joinEvent() {
        val currentUser = authRepository.currentUser ?: return
        val event = _uiState.value.event ?: return

        if (_uiState.value.isFull || _uiState.value.isCurrentUserParticipant) return

        viewModelScope.launch {
            _uiState.update { it.copy(isJoining = true) }

            // Create new participant from current user
            val newParticipant = Participant(
                id = currentUser.uid,
                name = currentUser.displayName ?: "",
                profileUrl = currentUser.photoURL ?: ""
            )
            val newParticipants = event.participants + newParticipant
            val updates = mapOf(
                "participants" to newParticipants.map { 
                    mapOf("id" to it.id, "name" to it.name, "profileUrl" to it.profileUrl)
                },
                "currentPlayers" to newParticipants.size
            )

            firestoreRepository.updateDocument(CollectionNames.EVENTS, eventId, updates)
                .onSuccess {
                    _uiState.update { it.copy(isJoining = false) }
                    _events.emit(EventDetailsEvent.JoinSuccess)
                    loadEvent() // Refresh data
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isJoining = false) }
                    _events.emit(EventDetailsEvent.Error(e.message ?: "Failed to join event"))
                }
        }
    }

    fun leaveEvent() {
        val userId = currentUserId ?: return
        val event = _uiState.value.event ?: return

        if (!_uiState.value.isCurrentUserParticipant || _uiState.value.isCurrentUserHost) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLeaving = true) }

            val newParticipants = event.participants.filter { it.id != userId }
            val updates = mapOf(
                "participants" to newParticipants.map { 
                    mapOf("id" to it.id, "name" to it.name, "profileUrl" to it.profileUrl)
                },
                "currentPlayers" to newParticipants.size
            )

            firestoreRepository.updateDocument(CollectionNames.EVENTS, eventId, updates)
                .onSuccess {
                    _uiState.update { it.copy(isLeaving = false) }
                    _events.emit(EventDetailsEvent.LeaveSuccess)
                    loadEvent() // Refresh data
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLeaving = false) }
                    _events.emit(EventDetailsEvent.Error(e.message ?: "Failed to leave event"))
                }
        }
    }
}
