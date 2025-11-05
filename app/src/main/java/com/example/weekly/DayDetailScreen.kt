package com.example.weekly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weekly.data.Note
import com.example.weekly.data.NoteViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.LocalTime // ⭐️ Важный импорт для onSaveNote


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    selectedDay: String,
    noteViewModel: NoteViewModel,
    onBack: () -> Unit
) {
    val allNotes by noteViewModel.notesGroupedByDay.collectAsState()

    val dayNotes = allNotes[selectedDay] ?: emptyList()

    var showDialog by remember { mutableStateOf(false) }
    var noteToEdit: Note? by remember { mutableStateOf(null) }
    var pendingNoteType: NoteType? by remember { mutableStateOf(null) }

    fun openCreationDialog(type: NoteType?) {
        noteToEdit = null
        pendingNoteType = type
        showDialog = true
    }

    fun openEditDialog(note: Note?) {
        noteToEdit = note
        pendingNoteType = null
        showDialog = true
    }

    val displayDate = try {
        val date = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
        val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))
        val dateDisplay = date.format(DATE_FORMAT_DISPLAY)
        "$dayName, $dateDisplay"
    } catch (e: Exception) {
        selectedDay
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text(displayDate) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FabContainer(
                onAddTask = { openCreationDialog(NoteType.TASK) },
                onAddNote = { openCreationDialog(NoteType.NOTE) }
            )
        }
    ) { padding ->
        NoteList(
            modifier = Modifier.padding(padding),
            notes = dayNotes,
            onDeleteNote = { note -> noteViewModel.deleteNote(note) },
            onEditNote = { note -> openEditDialog(note) },
            onToggleDone = { note -> noteViewModel.toggleDoneStatus(note) }
        )

        if (showDialog) {
            val isTask = noteToEdit?.startTime != null || pendingNoteType == NoteType.TASK

            AddNoteDialog(
                noteToEdit = noteToEdit,
                isTask = isTask,
                defaultDay = selectedDay,
                onDismiss = { showDialog = false; noteToEdit = null; pendingNoteType = null },
                // ⭐️ Исправленный вызов с явно указанными типами
                onSaveNote = { id: Int, day: String, content: String, startTime: LocalTime? ->
                    noteViewModel.saveNote(id, day, content, startTime)
                    showDialog = false
                    noteToEdit = null
                    pendingNoteType = null
                }
            )
        }
    }
}


// КОМПОНЕНТ FAB CONTAINER - ДВЕ КНОПКИ
@Composable
fun FabContainer(
    onAddTask: () -> Unit,
    onAddNote: () -> Unit
) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        ExtendedFloatingActionButton(
            onClick = onAddNote,
            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
            text = { Text("Заметка", style = MaterialTheme.typography.labelLarge) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )

        ExtendedFloatingActionButton(
            onClick = onAddTask,
            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            text = { Text("Дело", style = MaterialTheme.typography.labelLarge) },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}