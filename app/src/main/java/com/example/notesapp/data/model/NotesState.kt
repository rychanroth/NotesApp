package com.example.notesapp.data.model

/**
 * Represent the state of the notes list.
 */
sealed class NotesState {
    // Initial state: nothing loaded yet.
    object Idle: NotesState()

    // Loading state: fetching notes from firebase.
    object Loading: NotesState()

    // Success state: notes loaded successfully
    data class Success(val notes: List<Note>) : NotesState()

    // Error state: notes failed to load
    data class Error(val message: String) : NotesState()

}