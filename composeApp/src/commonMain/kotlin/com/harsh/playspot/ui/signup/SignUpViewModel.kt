package com.harsh.playspot.ui.signup

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

data class SignUpUiState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val agreedToTerms: Boolean = false,
    val termsError: String? = null
)

sealed class SignUpEvent {
    data object SignUpSuccess : SignUpEvent()
    data class SignUpError(val message: String) : SignUpEvent()
}

class SignUpViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(SignUpUiState())
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<SignUpEvent>()
    val events: SharedFlow<SignUpEvent> = _events.asSharedFlow()

    fun onFullNameChange(fullName: String) {
        _uiState.update {
            it.copy(fullName = fullName, fullNameError = null)
        }
    }

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = null, confirmPasswordError = null)
        }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _uiState.update {
            it.copy(confirmPassword = confirmPassword, confirmPasswordError = null)
        }
    }

    fun onTermsAgreedChange(agreed: Boolean) {
        _uiState.update {
            it.copy(agreedToTerms = agreed, termsError = null)
        }
    }

    fun signUp() {
        val currentState = _uiState.value

        // Validate inputs
        var hasError = false
        var fullNameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null
        var termsError: String? = null

        if (currentState.fullName.isBlank()) {
            fullNameError = "Full name is required"
            hasError = true
        }

        if (currentState.email.isBlank()) {
            emailError = "Email is required"
            hasError = true
        } else if (!isValidEmail(currentState.email)) {
            emailError = "Invalid email format"
            hasError = true
        }

        if (currentState.password.isBlank()) {
            passwordError = "Password is required"
            hasError = true
        } else if (currentState.password.length < 6) {
            passwordError = "Password must be at least 6 characters"
            hasError = true
        }

        if (currentState.confirmPassword.isBlank()) {
            confirmPasswordError = "Please confirm your password"
            hasError = true
        } else if (currentState.password != currentState.confirmPassword) {
            confirmPasswordError = "Passwords do not match"
            hasError = true
        }

        if (!currentState.agreedToTerms) {
            termsError = "You must agree to the terms and conditions"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    fullNameError = fullNameError,
                    emailError = emailError,
                    passwordError = passwordError,
                    confirmPasswordError = confirmPasswordError,
                    termsError = termsError
                )
            }
            return
        }

        // Perform sign up
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            authRepository.signUpWithEmail(currentState.email, currentState.password)
                .onSuccess { user ->
                    // Update display name after successful signup
                    // Note: Display name update might need additional implementation
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(SignUpEvent.SignUpSuccess)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(SignUpEvent.SignUpError(exception.message ?: "Sign up failed"))
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}
