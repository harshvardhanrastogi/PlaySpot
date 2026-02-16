package com.harsh.playspot.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harsh.playspot.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null
)

sealed class ForgotPasswordEvent {
    data object EmailSent : ForgotPasswordEvent()
    data class Error(val message: String) : ForgotPasswordEvent()
}

class ForgotPasswordViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<ForgotPasswordEvent>()
    val events: SharedFlow<ForgotPasswordEvent> = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null)
        }
    }

    fun sendResetLink() {
        val email = _uiState.value.email.trim()

        // Validate email
        if (email.isBlank()) {
            _uiState.update {
                it.copy(emailError = "Email is required")
            }
            return
        }

        if (!isValidEmail(email)) {
            _uiState.update {
                it.copy(emailError = "Invalid email format")
            }
            return
        }

        // Send password reset email
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            authRepository.sendPasswordResetEmail(email)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(ForgotPasswordEvent.EmailSent)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(ForgotPasswordEvent.Error(exception.message ?: "Failed to send reset email"))
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}
