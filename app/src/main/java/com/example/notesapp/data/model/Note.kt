package com.example.notesapp.data.model

/**
 * Represent a single note in the app.
 * Each note belongs to a specific user.
 */
data class Note(
    val id: String = "",
    val userId: String = "",
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)