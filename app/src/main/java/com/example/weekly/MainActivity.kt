package com.example.weekly

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weekly.data.*
import com.example.weekly.ui.theme.WEEKLYTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

// ******************************************************
// ГЛАВНАЯ ACTIVITY И NAV HOST
// ******************************************************

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val application = LocalContext.current.applicationContext as WeeklyApplication

            WEEKLYTheme {
                WeeklyNavHost(
                    noteViewModel = viewModel(
                        factory = NoteViewModelFactory(application.repository)
                    )
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyNavHost(noteViewModel: NoteViewModel) {
    val navController = rememberNavController()

    // ⭐️ Получаем сгруппированные заметки
    val notesGroupedByDay by noteViewModel.notesGroupedByDay.collectAsState()

    Scaffold { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Planner.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            // 1. Главный экран: Список дней (PlannerScreen)
            composable(Screen.Planner.route) {
                DayListScreen(
                    onDayClick = { day ->
                        navController.navigate(Screen.DayDetail.createRoute(day))
                    },
                    groupedNotes = notesGroupedByDay // ⭐️ Передаем данные
                )
            }

            // 2. Экран деталей дня: Заметки
            composable(
                route = Screen.DayDetail.route,
                arguments = listOf(navArgument("day") { type = NavType.StringType })
            ) { backStackEntry ->
                val day = backStackEntry.arguments?.getString("day") ?: return@composable

                DayDetailScreen(
                    selectedDay = day,
                    noteViewModel = noteViewModel,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// ⭐️ ЭКРАН 1: СПИСОК ДНЕЙ (ОБНОВЛЕН ДЛЯ ОТОБРАЖЕНИЯ ДАТЫ И АНОНСА)
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayListScreen(
    onDayClick: (String) -> Unit,
    groupedNotes: Map<String, List<Note>> // ⭐️ Принимаем сгруппированные заметки
) {
    val daysOfWeek = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")

    // Определяем начало текущей недели (понедельник)
    val startOfWeek = LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))

    // Создаем список пар (ДеньНедели, Дата) для текущей недели
    val weekDaysWithDates = remember {
        daysOfWeek.mapIndexed { index, dayName ->
            dayName to startOfWeek.plusDays(index.toLong())
        }
    }

    val today = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE")) // День недели на русском

    Scaffold(
        topBar = { TopAppBar(title = { Text("📝 Еженедельник") }) }
    ) { paddingValues ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            items(weekDaysWithDates) { (dayName, date) ->

                val isToday = dayName == today
                val notes = groupedNotes[dayName] ?: emptyList()

                // Получаем первую заметку для анонса
                val noteSnippet = notes.firstOrNull()?.content ?: "Нет запланированных дел."

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onDayClick(dayName) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isToday) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = if (isToday) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            // ⭐️ Отображение даты
                            Text(
                                text = date.format(DateTimeFormatter.ofPattern("dd.MM")),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        // ⭐️ Отображение анонса заметки
                        Text(
                            text = noteSnippet,
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// ⭐️ ЭКРАН 2: ДЕТАЛИ ДНЯ (ОБНОВЛЕН ДЛЯ РЕАЛЬНОГО ОТОБРАЖЕНИЯ ЗАМЕТОК)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    selectedDay: String,
    noteViewModel: NoteViewModel,
    onBack: () -> Unit
) {
    // ⭐️ Используем сгруппированные заметки
    val allNotes by noteViewModel.notesGroupedByDay.collectAsState()

    // Фильтруем заметки для текущего дня
    val dayNotes = allNotes[selectedDay] ?: emptyList()

    var showDialog by remember { mutableStateOf(false) }
    var noteToEdit: Note? by remember { mutableStateOf(null) }

    fun openEditDialog(note: Note?) {
        noteToEdit = note
        showDialog = true
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(selectedDay) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { openEditDialog(null) }) {
                Icon(Icons.Filled.Add, contentDescription = "Добавить заметку")
            }
        }
    ) { padding ->
        NoteList(
            modifier = Modifier.padding(padding),
            notes = dayNotes, // ⭐️ Передаем только заметки для этого дня
            onDeleteNote = { note -> noteViewModel.deleteNote(note) },
            onEditNote = { note -> openEditDialog(note) },
            // ⭐️ Добавлено переключение статуса isDone
            onToggleDone = { note -> noteViewModel.toggleDoneStatus(note) }
        )

        if (showDialog) {
            AddNoteDialog(
                noteToEdit = noteToEdit,
                defaultDay = selectedDay, // ⭐️ Используем текущий день
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

// ******************************************************
// ОБНОВЛЕННЫЕ КОМПОНЕНТЫ
// ******************************************************

// NoteList: Добавлен onToggleDone
@Composable
fun NoteList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onDeleteNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit,
    onToggleDone: (Note) -> Unit // ⭐️ НОВАЯ ФУНКЦИЯ
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Сортируем: сначала невыполненные, затем выполненные
        items(notes.sortedBy { it.isDone }, key = { it.id }) { note ->
            NoteItem(
                note = note,
                onEdit = { onEditNote(note) },
                onDelete = { onDeleteNote(note) },
                onToggleDone = { onToggleDone(note) } // ⭐️ Передаем функцию
            )
            Divider()
        }
    }
    if (notes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("В этот день пока нет заметок.")
        }
    }
}

// NoteItem: Добавлен Checkbox и логика зачеркивания
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit, onToggleDone: () -> Unit) {
    val cardAlpha = if (note.isDone) 0.6f else 1.0f
    val textDecoration = if (note.isDone) TextDecoration.LineThrough else null

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .alpha(cardAlpha) // Уменьшаем прозрачность для выполненных
            .combinedClickable(onClick = onEdit)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ⭐️ Checkbox
            Checkbox(
                checked = note.isDone,
                onCheckedChange = { onToggleDone() },
                modifier = Modifier.padding(end = 8.dp)
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.day,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = textDecoration // ⭐️ Зачеркивание
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = note.content,
                    style = MaterialTheme.typography.bodyMedium,
                    textDecoration = textDecoration // ⭐️ Зачеркивание
                )
            }
            // Кнопка удаления
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить заметку")
            }
        }
    }
}

// AddNoteDialog: Обновлен, чтобы принимать defaultDay и отключать DaySelector
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    noteToEdit: Note?,
    defaultDay: String, // ⭐️ Принимаем день по умолчанию
    onDismiss: () -> Unit,
    onSaveNote: (id: Int, day: String, content: String) -> Unit
) {
    val isEditing = noteToEdit != null
    // День всегда берется из defaultDay, независимо от того, редактируем мы или создаем
    val selectedDay = defaultDay

    val initialContent = noteToEdit?.content ?: ""
    val noteId = noteToEdit?.id ?: 0

    var noteContent by remember { mutableStateOf(initialContent) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (isEditing) "Редактировать Заметку" else "Добавить Заметку") },
        text = {
            Column {
                // ⭐️ ОТОБРАЖАЕМ ЗАФИКСИРОВАННЫЙ ДЕНЬ (опционально, для ясности)
                Text(
                    text = "День: $selectedDay",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

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

