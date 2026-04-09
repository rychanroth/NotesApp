package com.example.notesapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import kotlinx.coroutines.tasks.await
import com.example.notesapp.data.model.Result

/**
 * Handles all authentication-related Firebase operations.
 * Single source of truth for data.
 */
class AuthRepository {
    // Firebase auth instance (will be injected later)
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Get the currently logged-in user's ID, if null returns null
     */
    val currentUserId: String?
        get() = auth.currentUser?.uid

    /**
     * Check if user is currently logged in.
     */
    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    /**
     * Register a new user with email and password.
     * @return Result.Success if successful, otherwise Result.Error
     */
    suspend fun register(email: String, password: String): Result<Unit> {
        return try {
            auth.createUserWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Error(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Login an existing user with email and password.
     * @return Result.Success if successful, otherwise Result.Error
     */
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Error(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Send a password reset email to the given email address.
     * @return Result.Success if successful, otherwise Result.Error
     */
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Error(Exception(getAuthErrorMessage(e)))
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Log out of the current user.
     */
    fun logout() {
        auth.signOut()
    }

    // ================================================
    /**
     * Convert Firebase error codes to understandable messages.
     */
    private fun getAuthErrorMessage(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address format."
            "ERROR_USER_NOT_FOUND" -> "No account found with this email."
            "ERROR_WRONG_PASSWORD" -> "Incorrect Password."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters."
            "ERROR_TOO_MANY_REQUESTS" -> "Too many failed attempts. Please try again later."
            else -> e.message ?: "Authentication failed."
        }
    }
}