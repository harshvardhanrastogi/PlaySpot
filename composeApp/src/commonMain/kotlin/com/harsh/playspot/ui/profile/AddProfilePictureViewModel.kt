package com.harsh.playspot.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

data class AddProfilePictureUiState(
    val selectedImageBytes: ByteArray? = null,
    val isLoading: Boolean = false,
    val uploadedImageUrl: String? = null,
    val errorMessage: String? = null,
    val isImageKitConfigured: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as AddProfilePictureUiState
        if (selectedImageBytes != null) {
            if (other.selectedImageBytes == null) return false
            if (!selectedImageBytes.contentEquals(other.selectedImageBytes)) return false
        } else if (other.selectedImageBytes != null) return false
        if (isLoading != other.isLoading) return false
        if (uploadedImageUrl != other.uploadedImageUrl) return false
        if (errorMessage != other.errorMessage) return false
        if (isImageKitConfigured != other.isImageKitConfigured) return false
        return true
    }

    override fun hashCode(): Int {
        var result = selectedImageBytes?.contentHashCode() ?: 0
        result = 31 * result + isLoading.hashCode()
        result = 31 * result + (uploadedImageUrl?.hashCode() ?: 0)
        result = 31 * result + (errorMessage?.hashCode() ?: 0)
        result = 31 * result + isImageKitConfigured.hashCode()
        return result
    }
}

sealed class ProfilePictureEvent {
    data object UploadSuccess : ProfilePictureEvent()
    data class UploadError(val message: String) : ProfilePictureEvent()
}

class AddProfilePictureViewModel(
    private val imageKitRepository: ImageKitRepository = ImageKitRepository.getInstance(),
    private val firestoreRepository: FirestoreRepository = FirestoreRepository.instance,
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddProfilePictureUiState())
    val uiState: StateFlow<AddProfilePictureUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ProfilePictureEvent>()
    val events: SharedFlow<ProfilePictureEvent> = _events.asSharedFlow()

    init {
        // Check if ImageKit is configured
        _uiState.update { 
            it.copy(isImageKitConfigured = imageKitRepository.isConfigured()) 
        }
    }

    fun onImageSelected(imageBytes: ByteArray) {
        _uiState.update { 
            it.copy(
                selectedImageBytes = imageBytes,
                errorMessage = null
            ) 
        }
    }

    fun clearImage() {
        _uiState.update { 
            it.copy(
                selectedImageBytes = null,
                uploadedImageUrl = null,
                errorMessage = null
            ) 
        }
    }

    /**
     * Upload the selected image to ImageKit and save the URL to Firestore
     */
    fun uploadProfilePicture(profileDocumentId: String? = null) {
        val imageBytes = _uiState.value.selectedImageBytes
        if (imageBytes == null) {
            _uiState.update { it.copy(errorMessage = "No image selected") }
            return
        }

        if (!imageKitRepository.isConfigured()) {
            _uiState.update { 
                it.copy(errorMessage = "ImageKit is not configured. Please add your API keys to local.properties") 
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val userId = authRepository.currentUser?.uid ?: "unknown"
            
            val result = imageKitRepository.uploadProfilePicture(
                imageBytes = imageBytes,
                userId = userId
            )

            result.onSuccess { response ->
                // Save the URL to Firestore profile
                val docId = profileDocumentId ?: userId
                try {
                    firestoreRepository.updateDocument(
                        collection = CollectionNames.USER_PROFILE,
                        documentId = docId,
                        updates = mapOf("profilePictureUrl" to response.url)
                    )
                    
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            uploadedImageUrl = response.url
                        ) 
                    }
                    _events.emit(ProfilePictureEvent.UploadSuccess)
                } catch (e: Exception) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            errorMessage = "Image uploaded but failed to save to profile: ${e.message}"
                        ) 
                    }
                }
            }.onFailure { error ->
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        errorMessage = "Upload failed: ${error.message}"
                    ) 
                }
                _events.emit(ProfilePictureEvent.UploadError(error.message ?: "Unknown error"))
            }
        }
    }

    /**
     * Get an optimized URL for displaying the profile picture
     */
    fun getOptimizedProfileUrl(url: String, size: Int = 200): String {
        return imageKitRepository.getProfilePictureUrl(url, size)
    }
}
