package com.harsh.playspot.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.dao.Profile
import com.harsh.playspot.data.auth.AuthRepository
import com.harsh.playspot.data.firestore.CollectionNames
import com.harsh.playspot.data.firestore.FirestoreRepository
import com.harsh.playspot.data.imagekit.ImageKitRepository
import com.harsh.playspot.util.LocationProvider
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
    val isSaving: Boolean = false,
    val isEditing: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val name: String = "",
    val username: String = "",
    val location: String = "",
    val bio: String = "",
    val editedBio: String = "",
    val skillLevel: String = "",
    val editedSkillLevel: String = "",
    val playTimes: List<String> = emptyList(),
    val editedPlayTimes: List<String> = emptyList(),
    val preferredSports: List<String> = emptyList(),
    val profilePictureUrl: String? = null
)

sealed class ProfileEvent {
    data object LogoutSuccess : ProfileEvent()
    data class LogoutError(val message: String) : ProfileEvent()
    data object SaveSuccess : ProfileEvent()
    data class SaveError(val message: String) : ProfileEvent()
}

class ProfileViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance(),
    private val locationProvider: LocationProvider = LocationProvider.getInstance()
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
                // Get optimized profile picture URL if available
                val optimizedPictureUrl = profile?.profilePictureUrl?.takeIf { it.isNotBlank() }?.let {
                    imageKitRepository.getProfilePictureUrl(it, size = 256)
                }
                
                // Get location - use profile city if available, otherwise fetch current location
                val locationString = if (profile?.city?.isNotBlank() == true) {
                    profile.city
                } else {
                    fetchCurrentLocationString()
                }
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        name = profile?.fullName ?: "",
                        username = "@${profile?.userName ?: ""}",
                        location = locationString,
                        bio = profile?.bio ?: "",
                        skillLevel = profile?.skillLevel ?: "",
                        playTimes = profile?.playTime ?: emptyList(),
                        preferredSports = profile?.preferredSports ?: emptyList(),
                        profilePictureUrl = optimizedPictureUrl
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    
    private suspend fun fetchCurrentLocationString(): String {
        return try {
            if (!locationProvider.hasLocationPermission()) {
                return ""
            }
            
            val location = locationProvider.getCurrentLocation() ?: return ""
            val address = locationProvider.reverseGeocode(location.latitude, location.longitude) ?: return ""
            val locationDisplay = address.toDisplayString()
            
            // Save to user profile
            if (locationDisplay.isNotBlank()) {
                saveLocationToProfile(locationDisplay, location.latitude, location.longitude)
            }
            
            locationDisplay
        } catch (e: Exception) {
            ""
        }
    }
    
    private suspend fun saveLocationToProfile(city: String, latitude: Double, longitude: Double) {
        val uid = authRepository.currentUser?.uid ?: return
        val updates = mapOf(
            "city" to city,
            "latitude" to latitude,
            "longitude" to longitude
        )
        firestoreRepository.updateDocument(
            collection = CollectionNames.USER_PROFILE,
            documentId = uid,
            updates = updates
        )
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

    fun startEditing() {
        _uiState.update {
            it.copy(
                isEditing = true,
                editedBio = it.bio,
                editedSkillLevel = it.skillLevel,
                editedPlayTimes = it.playTimes
            )
        }
    }

    fun cancelEditing() {
        _uiState.update {
            it.copy(
                isEditing = false,
                hasUnsavedChanges = false,
                editedBio = "",
                editedSkillLevel = "",
                editedPlayTimes = emptyList()
            )
        }
    }

    fun onBioChange(bio: String) {
        _uiState.update {
            it.copy(
                editedBio = bio,
                hasUnsavedChanges = true
            )
        }
    }

    fun onSkillLevelChange(skillLevel: String) {
        _uiState.update {
            it.copy(
                editedSkillLevel = skillLevel,
                hasUnsavedChanges = true
            )
        }
    }

    fun onPlayTimeToggle(playTime: String) {
        _uiState.update { state ->
            val currentPlayTimes = state.editedPlayTimes.toMutableList()
            if (currentPlayTimes.contains(playTime)) {
                currentPlayTimes.remove(playTime)
            } else {
                currentPlayTimes.add(playTime)
            }
            state.copy(
                editedPlayTimes = currentPlayTimes,
                hasUnsavedChanges = true
            )
        }
    }

    fun saveChanges() {
        val uid = authRepository.currentUser?.uid ?: return

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val updates = mapOf(
                "bio" to _uiState.value.editedBio,
                "skillLevel" to _uiState.value.editedSkillLevel,
                "playTime" to _uiState.value.editedPlayTimes
            )

            firestoreRepository.updateDocument(
                collection = CollectionNames.USER_PROFILE,
                documentId = uid,
                updates = updates
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        isEditing = false,
                        hasUnsavedChanges = false,
                        bio = it.editedBio,
                        skillLevel = it.editedSkillLevel,
                        playTimes = it.editedPlayTimes
                    )
                }
                _events.emit(ProfileEvent.SaveSuccess)
            }.onFailure { exception ->
                _uiState.update { it.copy(isSaving = false) }
                _events.emit(ProfileEvent.SaveError(exception.message ?: "Failed to save changes"))
            }
        }
    }
}
