package com.harsh.playspot.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Profile
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

data class ProfileUiState(
    val isLoggingOut: Boolean = false,
    val isLoading: Boolean = true,
    val name: String = "",
    val username: String = "",
    val location: String = "",
    val bio: String = "",
    val skillLevel: String = "",
    val playTimes: List<String> = emptyList(),
    val preferredSports: List<String> = emptyList()
)

sealed class ProfileEvent {
    data object LogoutSuccess : ProfileEvent()
    data class LogoutError(val message: String) : ProfileEvent()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfileEvent>()
    val events: SharedFlow<ProfileEvent> = _events.asSharedFlow()

    init {
        loadProfile()
    }

    fun refreshProfile() {
        loadProfile()
    }

    private fun loadProfile() {
        val uid = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            firestoreRepository.getDocument<Profile>(
                collection = CollectionNames.USER_PROFILE,
                documentId = uid
            ).onSuccess { profile ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = profile?.fullName ?: "",
                        username = "@${profile?.userName ?: ""}",
                        location = profile?.city ?: "",
                        bio = profile?.bio ?: "",
                        skillLevel = profile?.skillLevel ?: "",
                        playTimes = profile?.playTime ?: emptyList(),
                        preferredSports = profile?.preferredSports ?: emptyList()
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }

            authRepository.signOut()
                .onSuccess {
                    _uiState.update { it.copy(isLoggingOut = false) }
                    _events.emit(ProfileEvent.LogoutSuccess)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoggingOut = false) }
                    _events.emit(ProfileEvent.LogoutError(exception.message ?: "Logout failed"))
                }
        }
    }
}
