package com.harsh.playspot.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.EventStatus
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.data.imagekit.ImageKitRepository
import com.harsh.playspot.ui.core.SportColors
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ExploreUiState(
    val isLoading: Boolean = false,
    val recommendedMatches: List<RecommendedMatch> = emptyList(),
    val error: String? = null
)

class ExploreViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState: StateFlow<ExploreUiState> = _uiState.asStateFlow()

    init {
        loadEvents()
    }

    fun loadEvents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val currentUserId = authRepository.currentUser?.uid
            
            // Query only upcoming events from Firestore (filter at database level)
            firestoreRepository.queryDocuments<Event>(
                collection = CollectionNames.EVENTS,
                field = "status",
                value = EventStatus.UPCOMING
            )
                .onSuccess { events ->
                    // Filter out:
                    // 1. Events created by current user
                    // 2. Events where current user is already a participant
                    val recommendedMatches = events
                        .filter { event -> 
                            event.creatorId != currentUserId &&
                            event.participants.none { it.id == currentUserId }
                        }
                        .map { event -> event.toRecommendedMatch() }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recommendedMatches = recommendedMatches
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load events"
                        )
                    }
                }
        }
    }

    private fun Event.toRecommendedMatch(): RecommendedMatch {
        // Determine match status based on player count
        val status = when {
            playerLimit > 0 && currentPlayers >= playerLimit -> MatchStatus.Full
            playerLimit > 0 && (playerLimit - currentPlayers) <= 2 -> 
                MatchStatus.SpotsLeft(playerLimit - currentPlayers)
            playerLimit > 0 -> MatchStatus.Attending(currentPlayers, playerLimit)
            else -> MatchStatus.Open
        }

        // Format date and time
        val formattedDateTime = if (time.isNotBlank()) "$date • $time" else date

        // Get sport color from shared SportColors
        val sportColor = SportColors.getColor(sportType)

        // Derive tag from skill level
        val tag = skillLevel.ifBlank { "Open" }

        // Create tags list from skill level
        val tags = if (skillLevel.isNotBlank()) listOf(skillLevel) else emptyList()

        // Get optimized cover image URL for 112dp display
        val optimizedCoverUrl = if (coverImageUrl.isNotBlank()) {
            imageKitRepository.getExploreCoverUrl(coverImageUrl)
        } else ""

        // Get optimized avatar URLs for participants (up to 4 for display)
        // Include empty strings for participants without profile URLs so fallback initials can be shown
        val avatarUrls = participants
            .take(4)
            .map { participant ->
                if (participant.profileUrl.isNotBlank()) {
                    imageKitRepository.getAvatarUrl(participant.profileUrl)
                } else ""
            }

        return RecommendedMatch(
            id = id,
            title = matchName,
            sport = sportType,
            sportColor = sportColor,
            date = formattedDateTime,
            location = venue.name,
            distance = "", // Distance calculation requires user location - keep empty
            tag = tag,
            tagIsPrimary = false,
            status = status,
            attendees = currentPlayers,
            maxAttendees = playerLimit,
            tags = tags,
            coverImageUrl = optimizedCoverUrl,
            participantAvatars = avatarUrls
        )
    }
}
