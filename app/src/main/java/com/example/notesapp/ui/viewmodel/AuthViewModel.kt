package com.example.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.AuthState
import com.example.notesapp.data.model.Result
import com.example.notesapp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for authethication screens.
 * Manage auth state and handles login/register/reset password/logout operations.
 */
class AuthViewModel(
     private val repository: AuthRepository = AuthRepository()
): ViewModel() {
    // Private mutable state
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)

    // Public immutable state (read-only for UI)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        // Check if user is already logged in
        if (repository.isLoggedIn) {
            _authState.value = AuthState.Authenticated
        }
    }

    /**
     * Register user's account.
     * TODO: EXPERIMENTAL
     */
    fun register(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            when (val result = repository.register(email, password)) {
                is Result.Success -> {
                    _authState.value = AuthState.Authenticated
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Failed to register"
                    )
                }
            }
        }
    }

    /**
     * Login with email and password.
     */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            when (val result = repository.login(email, password)) {
                is Result.Success -> {
                    _authState.value = AuthState.Authenticated
                }
                is Result.Error -> {
                    _authState.value = AuthState.Error(
                        result.exception.message ?: "Registration failed"
                    )
                }
            }
        }
    }

    /**
     * Send password reset email.
     */
    fun sendPasswordReset(email: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            when (val result = repository.sendPasswordResetEmail(email)) {
                is Result.Success -> {
                    onResult(true, "Password reset email sent!")
                }
                is Result.Error -> {
                    onResult(false, result.exception.message ?: "Failed to send reset email.")
                }
            }
        }
    }

    /**
     * Logout of the current user.
     */
    fun logout() {
        repository.logout()
        _authState.value = AuthState.Idle
    }

    /**
     * Reset state.
     * TODO: EXPERIMENTAL
     */
    fun resetState() {
        _authState.value = AuthState.Idle
    }

}