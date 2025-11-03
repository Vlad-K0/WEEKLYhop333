package com.example.weekly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weekly.data.* import com.example.weekly.ui.theme.WEEKLYTheme // Замените на имя вашей темы

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as WeeklyApplication

            WEEKLYTheme { // Используйте имя вашей темы
                PlannerScreen(
                    noteViewModel = viewModel(
                        factory = NoteViewModelFactory(application.repository)
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlannerScreen(noteViewModel: NoteViewModel) {
    val notes by noteViewModel.uiState.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var noteToEdit: Note? by remember { mutableStateOf(null) }

    fun openDialog(note: Note?) {
        noteToEdit = note
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📝 Еженедельник WEEKLY") })
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openDialog(null) }) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить заметку")
            }
        }
    ) { padding ->
        NoteList(
            modifier = Modifier.padding(padding),
            notes = notes,
            onDeleteNote = { note -> noteViewModel.deleteNote(note) },
            onEditNote = { note -> openDialog(note) }
        )

        if (showDialog) {
            AddNoteDialog(
                noteToEdit = noteToEdit,
                onDismiss = { showDialog = false; noteToEdit = null },
                onSaveNote = { id, day, content ->
                    noteViewModel.saveNote(id, day, content)
                    showDialog = false
                    noteToEdit = null
                }
            )
        }
    }
}

@Composable
fun NoteList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onDeleteNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(notes, key = { it.id }) { note ->
            NoteItem(
                note = note,
                onEdit = { onEditNote(note) },
                onDelete = { onDeleteNote(note) }
            )
            Divider()
        }
    }
    if (notes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Пока нет заметок. Нажмите '+' для добавления.")
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .combinedClickable(
                onClick = onEdit,          // Обычный клик открывает редактирование
                onLongClick = { /* Долгое нажатие не используется явно, используем кнопку */ }
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.day,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            // Кнопка удаления
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить заметку")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    noteToEdit: Note?,
    onDismiss: () -> Unit,
    onSaveNote: (id: Int, day: String, content: String) -> Unit
) {
    val isEditing = noteToEdit != null
    val initialDay = noteToEdit?.day ?: "Понедельник"
    val initialContent = noteToEdit?.content ?: ""
    val noteId = noteToEdit?.id ?: 0

    var selectedDay by remember { mutableStateOf(initialDay) }
    var noteContent by remember { mutableStateOf(initialContent) }
    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Редактировать Заметку" else "Добавить Заметку") },
        text = {
            Column {
                DaySelector(
                    daysOfWeek = daysOfWeek,
                    selectedDay = selectedDay,
                    onDaySelected = { selectedDay = it }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Текст заметки") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteContent.isNotBlank()) {
                        onSaveNote(noteId, selectedDay, noteContent.trim())
                    }
                },
                enabled = noteContent.isNotBlank()
            ) {
                Text(if (isEditing) "Сохранить" else "Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DaySelector(daysOfWeek: List<String>, selectedDay: String, onDaySelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth().menuAnchor(),
            readOnly = true,
            value = selectedDay,
            onValueChange = { },
            label = { Text("День недели") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = ExposedDropdownMenuDefaults.textFieldColors()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            daysOfWeek.forEach { day ->
                DropdownMenuItem(
                    text = { Text(day) },
                    onClick = {
                        onDaySelected(day)
                        expanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                )
            }
        }
    }
}