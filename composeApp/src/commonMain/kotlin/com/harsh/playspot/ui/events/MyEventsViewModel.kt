package com.harsh.playspot.ui.events

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Event
import com.harsh.playspot.dao.UserEvent
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.data.imagekit.ImageKitRepository
import com.harsh.playspot.ui.core.SportColors
import com.harsh.playspot.ui.home.MatchStatus
import com.harsh.playspot.ui.home.RecommendedMatch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyEventsUiState(
    val isLoading: Boolean = false,
    val organizingEvents: List<RecommendedMatch> = emptyList(),
    val attendingEvents: List<RecommendedMatch> = emptyList(),
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0
)

interface EventManager {
    fun fetchEvents()
    fun refreshEvents()
}

class MyEventsViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance()
) : ViewModel(), EventManager {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        fetchEvents()
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    override fun refreshEvents() {
        fetchEvents()
    }

    override fun fetchEvents() {
        val currentUser = authRepository.currentUser
        if (currentUser == null) {
            _uiState.update { it.copy(errorMessage = "Please login to view your events") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // Fetch events where user is the creator (Organizing)
                val organizingResult = firestoreRepository.queryDocuments<Event>(
                    collection = CollectionNames.EVENTS,
                    field = "creatorId",
                    value = currentUser.uid
                )

                organizingResult
                    .onSuccess { events ->
                        // Sort by date (newest first) and convert to RecommendedMatch
                        val organizingMatches = events
                            .sortedByDescending { it.createdAt }
                            .map { event -> event.toRecommendedMatch(isCreator = true) }
                        _uiState.update {
                            it.copy(
                                organizingEvents = organizingMatches
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                errorMessage = e.message ?: "Failed to fetch organizing events"
                            )
                        }
                    }

                // Fetch events where user is a participant (Attending) from USER_EVENTS collection
                val attendingResult = firestoreRepository.queryDocuments<UserEvent>(
                    collection = CollectionNames.USER_EVENTS,
                    field = "userId",
                    value = currentUser.uid
                )

                attendingResult
                    .onSuccess { userEvents ->
                        // Fetch full event data for each UserEvent to get accurate attendee counts
                        // Filter out events created by the user (those belong in Organizing tab)
                        val attendingMatches = userEvents
                            .filter { !it.isCreator }
                            .sortedByDescending { it.joinedAt }
                            .mapNotNull { userEvent ->
                                // Fetch full event data from events collection
                                val eventResult = firestoreRepository.getDocument<Event>(
                                    collection = CollectionNames.EVENTS,
                                    documentId = userEvent.eventId
                                )
                                eventResult.getOrNull()?.let { event ->
                                    event.toRecommendedMatch(isCreator = false)
                                }
                            }
                        _uiState.update {
                            it.copy(

                                isLoading = false
                            )
                        }
                    }
                    .onFailure { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Failed to fetch attending events"
                            )
                        }
                    }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Failed to fetch events"
                    )
                }
            }
        }
    }

    /**
     * Convert Event to RecommendedMatch for display
     */
    private fun Event.toRecommendedMatch(isCreator: Boolean): RecommendedMatch {
        val sportColor = SportColors.getColor(sportType)
        val formattedDateTime = if (time.isNotBlank()) "$date • $time" else date
        
        // Get optimized cover image URL
        val optimizedCoverUrl = if (coverImageUrl.isNotBlank()) {
            imageKitRepository.getExploreCoverUrl(coverImageUrl)
        } else ""

        // Determine match status based on player count
        val status = when {
            playerLimit > 0 && currentPlayers >= playerLimit -> MatchStatus.Full
            playerLimit > 0 && (playerLimit - currentPlayers) <= 2 -> 
                MatchStatus.SpotsLeft(playerLimit - currentPlayers)
            playerLimit > 0 -> MatchStatus.Attending(currentPlayers, playerLimit)
            else -> MatchStatus.Open
        }

        // Build tags list
        val tags = buildList {
            if (skillLevel.isNotBlank()) add(skillLevel)
        }

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
            distance = "",
            tag = if (isCreator) "Organizing" else skillLevel.ifBlank { "Attending" },
            tagIsPrimary = isCreator,
            status = status,
            attendees = currentPlayers,
            maxAttendees = playerLimit,
            tags = tags,
            coverImageUrl = optimizedCoverUrl,
            participantAvatars = avatarUrls
        )
    }

    fun setPreferredTab(openOrganizingEvents: Boolean) {
        _uiState.update {
            it.copy(selectedTabIndex = if (openOrganizingEvents) 1 else 0)
        }
    }
}
