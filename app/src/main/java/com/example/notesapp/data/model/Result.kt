package com.example.notesapp.data.model

import androidx.lifecycle.liveData

/**
 * A generic result wrapper for async operations.
 * Success contains data, Error contains exceptions.
 */
sealed class Result<out T> {
    data class Success<out T>(val data: T): Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()

    // Helper functions
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }

    fun exceptionOrNull(): Exception? = when (this) {
        is Success -> null
        is Error -> exception
    }
}