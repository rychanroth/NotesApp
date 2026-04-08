package com.example.notesapp.data.model

/**
 * Represent the current authentication state.
 * Using a sealed class ensure we handle all possible states
 */
sealed class AuthState {
    // Initialize state - user not logged in, no action progress
    object Idle: AuthState()

    // Loading state - login/register in progress
    object Loading: AuthState()

    // Success state - user is authenticated
    object Authenticated: AuthState()

    // Error state: - something went wrong
    data class Error(val message: String) : AuthState()
}