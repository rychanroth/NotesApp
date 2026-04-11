package com.example.notesapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesapp.data.model.AuthState
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.NotesState
import com.example.notesapp.data.model.Result
import com.example.notesapp.data.repository.AuthRepository
import com.example.notesapp.data.repository.NotesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.System.currentTimeMillis

/**
 * ViewModel for notes list screen.
 * Manages notes state and CRUD operations.
 */
class NotesViewModel(
    private val notesRepository: NotesRepository = NotesRepository(),
    private val authRepository: AuthRepository = AuthRepository()
): ViewModel() {
    private val _notesState = MutableStateFlow<NotesState>(NotesState.Idle)
    val notesState: StateFlow<NotesState> = _notesState.asStateFlow()

    private val _selectedNote = MutableStateFlow<Note?>(null)
    val selectedNote: StateFlow<Note?> = _selectedNote.asStateFlow()

    /**
     * Load all notes from the current user.
     */
    fun loadNotes() {
        val userId = authRepository.currentUserId
        if (userId == null) {
            _notesState.value = NotesState.Error("User not logged in.")
            return
        }

        viewModelScope.launch {
            _notesState.value = NotesState.Loading

            when (val result = notesRepository.getNotes(userId)) {
                is Result.Success -> {
                    _notesState.value = NotesState.Success(result.data)
                }
                is Result.Error -> {
                    _notesState.value = NotesState.Error(result.exception.message ?: "Failed to load notes.")
                }
            }
        }
    }

    /**
     * Add a new note.
     */
    fun addNote(title: String, content: String) {
        val userId = authRepository.currentUserId ?: return

        viewModelScope.launch {
            val note = Note(
                userId = userId,
                title = title,
                content = content,
                createdAt = currentTimeMillis(),
                updatedAt = currentTimeMillis()
            )

            when (val result = notesRepository.addNote(note)) {
                is Result.Success -> {
                    loadNotes()
                }
                is Result.Error -> {
                    _notesState.value = NotesState.Error(result.exception.message ?: "Failed to load notes.")
                }
            }
        }
    }

    /**
     * Update an existing notes.
     */
    fun updateNote(note: Note) {
        viewModelScope.launch {
            val updatedNote = note.copy(
                updatedAt = System.currentTimeMillis()
            )

            when (notesRepository.updateNote(updatedNote)) {
                is Result.Success -> {
                    loadNotes()
                }
                is Result.Error -> {
                    // Do nothing
                }
            }
        }
    }

    /**
     * Delete a note.
     */
    fun deleteNote(noteId: String) {
        viewModelScope.launch {
            when (notesRepository.deleteNote(noteId)) {
                is Result.Success -> {
                    loadNotes()
                }
                is Result.Error -> {
                    // Do nothing
                }
            }
        }
    }

    /**
     * Select a note for editing.
     */
    fun selectNote(note: Note?) {
        _selectedNote.value = note
    }
}