package com.harsh.playspot.ui.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Event
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyEventsUiState(
    val isLoading: Boolean = false,
    val organizingEvents: List<Event> = emptyList(),
    val participatingEvents: List<Event> = emptyList(),
    val errorMessage: String? = null,
    val selectedTabIndex: Int = 0
)

class MyEventsViewModel(
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyEventsUiState())
    val uiState: StateFlow<MyEventsUiState> = _uiState.asStateFlow()

    init {
        fetchEvents()
    }

    fun onTabSelected(index: Int) {
        _uiState.update { it.copy(selectedTabIndex = index) }
    }

    fun refreshEvents() {
        fetchEvents()
    }

    private fun fetchEvents() {
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
                        // Sort by date (newest first)
                        val sortedEvents = events.sortedByDescending { it.createdAt }
                        _uiState.update { 
                            it.copy(
                                organizingEvents = sortedEvents,
                                isLoading = false
                            ) 
                        }
                    }
                    .onFailure { e ->
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                errorMessage = e.message ?: "Failed to fetch events"
                            ) 
                        }
                    }

                // TODO: Fetch events where user is a participant (Participating)
                // This would require a different query approach since participants is an array

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
}
