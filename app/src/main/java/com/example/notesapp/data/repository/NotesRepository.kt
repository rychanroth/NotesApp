package com.example.notesapp.data.repository

import com.example.notesapp.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Handles all Firestore operations for notes.
 * Each user can only access their own notes.
 */
class NotesRepository {
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    // CollectionPath should corresponds to the collection path of Firebase Console
    private val notesCollection = firestore.collection("notes")

    /**
     * Get all notes for the current user, ordered by updated time.
     */
    suspend fun getNotes(userId: String): Result<List<Note>> {
        return try {
            val snapshot = notesCollection
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .get()
                .await()

            val notes = snapshot.documents.map { document ->
                Note(
                    id = document.id,
                    userId = document.getString("userId") ?: "",
                    title = document.getString("title") ?: "",
                    content = document.getString("content") ?: "",
                    createdAt = document.getLong("createdAt") ?: 0L,
                    updatedAt = document.getLong("updatedAt") ?: 0L
                )
            }
            Result.Success(notes)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * At a new note for the current user.
     */
    suspend fun addNote(note: Note): Result<String> {
        return try {
            val documentRef = notesCollection.add(note).await()
            Result.Success(documentRef.id)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Update an existing note.
     */
    suspend fun updateNote(note: Note): Result<Unit>    {
        return try {
            notesCollection.document(note.id).set(note).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    /**
     * Delete a note by ID.
     */
    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            notesCollection.document(noteId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }


}