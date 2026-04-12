package com.example.notesapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.Note
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notesapp.Screen
import com.example.notesapp.data.model.Note
import com.example.notesapp.data.model.NotesState
import com.example.notesapp.ui.components.AddNoteDialog
import com.example.notesapp.ui.components.EditNoteDialog
import com.example.notesapp.ui.viewmodel.AuthViewModel
import com.example.notesapp.ui.viewmodel.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesListScreen(
    onLogOut: () -> Unit,
    notesViewModel: NotesViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    // Collect state
    val notesState by notesViewModel.notesState.collectAsState()
    val selectedNote by notesViewModel.selectedNote.collectAsState()

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var noteToDelete by remember { mutableStateOf<Note?>(null)}

    // Load notes when screen appears
    LaunchedEffect(Unit) {
        notesViewModel.loadNotes()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(">^< Notes >|<") },
                actions = {
                    IconButton(
                        onClick = {
                            authViewModel.logout()
                            onLogOut()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Logout"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "Add Note"
                )
            }
        }
    ) { innerPadding ->
        when (notesState) {
            is NotesState.Idle -> {
                // Do nohting, will transition to loading
            }
            is NotesState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is NotesState.Success -> {
                val notes = (notesState as NotesState.Success).notes
                if (notes.isEmpty()) {
                    EmptyState(innerPadding)
                } else {
                    NotesList(
                        notes = notes,
                        padding = innerPadding,
                        onEdit = { note -> notesViewModel.selectNote(note) },
                        onDelete = { note ->
                            noteToDelete = note
                            showDeleteDialog = true
                        }
                    )
                }
            }
            is NotesState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = (notesState as NotesState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                }

            }
        }
    }

    // Add note DIALOG
    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content ->
                notesViewModel.addNote(title, content)
                showAddDialog = false
            }
        )
    }

    // Edit note DIALOG
    if (selectedNote != null) {
        EditNoteDialog(
            note = selectedNote!!,
            onDismiss = { notesViewModel.selectNote(null) },
            onConfirm = { title, content ->
                val updatedNote = selectedNote!!.copy(
                    title = title,
                    content = content
                )
                notesViewModel.updateNote(updatedNote)
                notesViewModel.selectNote(null)
            }
        )
    }

    // Delete note confirmation DIALOG
    // Edit Note Dialog
    if (selectedNote != null) {
        EditNoteDialog(
            note = selectedNote!!,
            onDismiss = { notesViewModel.selectNote(null) },
            onConfirm = { title, content ->
                val updatedNote = selectedNote!!.copy(
                    title = title,
                    content = content
                )
                notesViewModel.updateNote(updatedNote)
                notesViewModel.selectNote(null)
            }
        )
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog && noteToDelete != null) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
                noteToDelete = null
            },
            title = { Text("Delete Note") },
            text = { Text("Delete \"${noteToDelete?.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        noteToDelete?.let { notesViewModel.deleteNote(it.id) }
                        showDeleteDialog = false
                        noteToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        noteToDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

}

/**
 * Empty state when no notes are created yet.
 */
@Composable
fun EmptyState(padding: PaddingValues) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector =  Icons.AutoMirrored.Filled.Note,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text="No notes yet!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Tap + to create your first note",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.outline
        )

    }
}

/**
 * List of notes created of the current user.
 */
@Composable
private fun NotesList(
    notes: List<Note>,
    padding: PaddingValues,
    onEdit: (Note) -> Unit,
    onDelete: (Note) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(notes, key = { it.id } ) { note ->
            NoteItem(
                note = note,
                onEdit = { onEdit(note) },
                onDelete = { onDelete(note) }
            )
        }
    }
}

/**
 * Display note as an item.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NoteItem(
    note: Note,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onEdit
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Title
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Content
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Delete BUTTON
            IconButton(onClick = onDelete ) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
