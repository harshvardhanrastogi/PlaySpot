package com.harsh.playspot.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PersonalDetailsUiState(
    val bio: String = "",
    val skillLevel: String = "Casual",
    val selectedPlayTimes: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

sealed class PersonalDetailsEvent {
    data object SaveSuccess : PersonalDetailsEvent()
    data class SaveError(val message: String) : PersonalDetailsEvent()
}

class PersonalDetailsViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(PersonalDetailsUiState())
    val uiState: StateFlow<PersonalDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PersonalDetailsEvent>()
    val events: SharedFlow<PersonalDetailsEvent> = _events.asSharedFlow()

    fun onBioChange(bio: String) {
        _uiState.update { it.copy(bio = bio) }
    }

    fun onSkillLevelChange(skillLevel: String) {
        _uiState.update { it.copy(skillLevel = skillLevel) }
    }

    fun togglePlayTime(playTime: String) {
        _uiState.update { state ->
            val newPlayTimes = if (state.selectedPlayTimes.contains(playTime)) {
                state.selectedPlayTimes - playTime
            } else {
                state.selectedPlayTimes + playTime
            }
            state.copy(selectedPlayTimes = newPlayTimes)
        }
    }

    fun saveProfile() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            viewModelScope.launch {
                _events.emit(PersonalDetailsEvent.SaveError("User not authenticated"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val currentState = _uiState.value
            val updates = mapOf(
                "bio" to currentState.bio,
                "skillLevel" to currentState.skillLevel,
                "playTime" to currentState.selectedPlayTimes.toList()
            )

            firestoreRepository.updateDocument(
                collection = CollectionNames.USER_PROFILE,
                documentId = uid,
                updates = updates
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(PersonalDetailsEvent.SaveSuccess)
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(
                    PersonalDetailsEvent.SaveError(
                        exception.message ?: "Failed to save profile"
                    )
                )
            }
        }
    }
}
