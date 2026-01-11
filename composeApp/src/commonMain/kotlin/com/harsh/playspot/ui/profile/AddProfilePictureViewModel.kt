package com.harsh.playspot.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AddProfilePictureUiState(
    val selectedImageBytes: ByteArray? = null,
    val isLoading: Boolean = false
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
        return true
    }

    override fun hashCode(): Int {
        var result = selectedImageBytes?.contentHashCode() ?: 0
        result = 31 * result + isLoading.hashCode()
        return result
    }
}

class AddProfilePictureViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(AddProfilePictureUiState())
    val uiState: StateFlow<AddProfilePictureUiState> = _uiState.asStateFlow()

    fun onImageSelected(imageBytes: ByteArray) {
        _uiState.update { it.copy(selectedImageBytes = imageBytes) }
    }

    fun clearImage() {
        _uiState.update { it.copy(selectedImageBytes = null) }
    }
}
