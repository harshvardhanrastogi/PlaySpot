package com.harsh.playspot.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Profile
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.ui.core.SportUi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PreferenceSetupUiState(
    val selectedSports: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val isLoadingExisting: Boolean = true
)

sealed class PreferenceSetupEvent {
    data object SaveSuccess : PreferenceSetupEvent()
    data class SaveError(val message: String) : PreferenceSetupEvent()
}

class PreferenceSetupViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreferenceSetupUiState())
    val uiState: StateFlow<PreferenceSetupUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<PreferenceSetupEvent>()
    val events: SharedFlow<PreferenceSetupEvent> = _events.asSharedFlow()

    init {
        loadExistingSports()
    }

    private fun loadExistingSports() {
        val uid = authRepository.currentUser?.uid ?: run {
            _uiState.update { it.copy(isLoadingExisting = false) }
            return
        }

        viewModelScope.launch {
            firestoreRepository.getDocument<Profile>(
                collection = CollectionNames.USER_PROFILE,
                documentId = uid
            ).onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        selectedSports = profile?.preferredSports?.toSet() ?: emptySet(),
                        isLoadingExisting = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoadingExisting = false) }
            }
        }
    }

    fun toggleSport(sport: String) {
        _uiState.update { state ->
            val newSelectedSports = if (state.selectedSports.contains(sport)) {
                state.selectedSports - sport
            } else {
                state.selectedSports + sport
            }
            state.copy(selectedSports = newSelectedSports)
        }
    }

    fun selectAll(sports: List<SportUi>) {
        _uiState.update { it.copy(selectedSports = sports.map { it.name }.toSet()) }
    }

    fun clearAll() {
        _uiState.update { it.copy(selectedSports = emptySet()) }
    }

    fun savePreferences() {
        val uid = authRepository.currentUser?.uid
        if (uid == null) {
            viewModelScope.launch {
                _events.emit(PreferenceSetupEvent.SaveError("User not authenticated"))
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val updates = mapOf(
                "preferredSports" to _uiState.value.selectedSports.toList()
            )

            firestoreRepository.updateDocument(
                collection = CollectionNames.USER_PROFILE,
                documentId = uid,
                updates = updates
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(PreferenceSetupEvent.SaveSuccess)
            }.onFailure { exception ->
                _uiState.update { it.copy(isLoading = false) }
                _events.emit(
                    PreferenceSetupEvent.SaveError(
                        exception.message ?: "Failed to save preferences"
                    )
                )
            }
        }
    }
}
