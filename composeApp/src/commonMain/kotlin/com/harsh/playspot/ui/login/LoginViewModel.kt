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

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val passwordError: String? = null
)

sealed class LoginEvent {
    data object LoginSuccess : LoginEvent()
    data class LoginError(val message: String) : LoginEvent()
}

class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>()
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onEmailChange(email: String) {
        _uiState.update {
            it.copy(email = email, emailError = null)
        }
    }

    fun onPasswordChange(password: String) {
        _uiState.update {
            it.copy(password = password, passwordError = null)
        }
    }

    fun login() {
        val currentState = _uiState.value

        // Validate inputs
        var hasError = false
        var emailError: String? = null
        var passwordError: String? = null

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

        if (hasError) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError)
            }
            return
        }

        // Perform login
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            authRepository.signInWithEmail(currentState.email, currentState.password)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(LoginEvent.LoginSuccess)
                }
                .onFailure { exception ->
                    _uiState.update { it.copy(isLoading = false) }
                    _events.emit(LoginEvent.LoginError(exception.message ?: "Login failed"))
                }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }
}
