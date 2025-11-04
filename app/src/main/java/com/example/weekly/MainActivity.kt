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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weekly.data.*
import com.example.weekly.ui.theme.WEEKLYTheme
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
import java.util.Locale

// ******************************************************
// КОНСТАНТЫ И ENUM
// ******************************************************

private val LOCALE_RU = Locale.forLanguageTag("ru-RU")

@RequiresApi(Build.VERSION_CODES.O)
private val DATE_FORMAT_DISPLAY = DateTimeFormatter.ofPattern("dd.MM")
@RequiresApi(Build.VERSION_CODES.O)
private val DATE_FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// Тип элемента, который мы добавляем
enum class NoteType {
    TASK, // Дело (со временем)
    NOTE // Заметка (без времени)
}

// ******************************************************
// МАРШРУТЫ НАВИГАЦИИ (Routes)
// ******************************************************


// ******************************************************
// ГЛАВНАЯ ACTIVITY И NAV HOST
// ******************************************************

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем Application и Repository
        val application = application as WeeklyApplication

        setContent {
            WEEKLYTheme {
                val navController = rememberNavController()

                WeeklyNavHost(
                    noteViewModel = viewModel(
                        factory = NoteViewModelFactory(application.repository)
                    ),
                    navController = navController
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyNavHost(noteViewModel: NoteViewModel, navController: NavHostController) {

    val groupedNotes by noteViewModel.notesGroupedByDay.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.DayList.route
    ) {
        composable(Screen.DayList.route) {
            DayListScreen(
                groupedNotes = groupedNotes,
                onDayClick = { dayISO ->
                    navController.navigate(Screen.DayDetail.createRoute(dayISO))
                }
            )
        }

        composable(
            route = Screen.DayDetail.route,
            arguments = listOf(navArgument("day") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDay = backStackEntry.arguments?.getString("day") ?: LocalDate.now().format(DATE_FORMAT_ISO)

            DayDetailScreen(
                selectedDay = selectedDay,
                noteViewModel = noteViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

// ******************************************************
// ЭКРАН 1: СПИСОК ДНЕЙ
// ******************************************************

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayListScreen(
    onDayClick: (String) -> Unit,
    groupedNotes: Map<String, List<Note>>
) {
    var currentWeekStart by remember {
        mutableStateOf(LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)))
    }

    val weekEnd = currentWeekStart.plusDays(6)
    val weekRange = "${currentWeekStart.format(DATE_FORMAT_DISPLAY)} – ${weekEnd.format(DATE_FORMAT_DISPLAY)}"

    val weekDaysWithDates = remember(currentWeekStart) {
        (0L..6L).map { offset ->
            currentWeekStart.plusDays(offset)
        }
    }

    val today = LocalDate.now()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("📝 Еженедельник") })
        }
    ) { paddingValues ->
        Column(modifier = Modifier.fillMaxSize().padding(paddingValues)) {

            // UI ДЛЯ НАВИГАЦИИ ПО НЕДЕЛЯМ
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentWeekStart = currentWeekStart.minusWeeks(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущая неделя")
                }

                Text(
                    text = weekRange,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(onClick = { currentWeekStart = currentWeekStart.plusWeeks(1) }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующая неделя")
                }
            }
            HorizontalDivider()

            // Список дней
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(weekDaysWithDates) { date ->

                    val dateStringISO = date.format(DATE_FORMAT_ISO)
                    val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))

                    val isToday = date.isEqual(today)
                    val notes = groupedNotes[dateStringISO] ?: emptyList()

                    // Используем startTime != null для фильтрации Дел
                    val noteSnippet = notes.sortedWith(
                        compareBy<Note> { it.isDone }
                            .thenBy { it.startTime }
                    ).filter { !it.isDone }.firstOrNull()?.content
                        ?: notes.firstOrNull()?.content
                        ?: "Нет запланированных дел."

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onDayClick(dateStringISO) },
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isToday -> MaterialTheme.colorScheme.primaryContainer
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
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
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = date.format(DATE_FORMAT_DISPLAY),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
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
}

// ******************************************************
// ЭКРАН 2: ДЕТАЛИ ДНЯ
// ******************************************************

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
    // ⭐️ НОВОЕ СОСТОЯНИЕ: Тип создаваемого элемента
    var pendingNoteType: NoteType? by remember { mutableStateOf(null) }

    fun openCreationDialog(type: NoteType?) {
        noteToEdit = null // Для создания
        pendingNoteType = type
        showDialog = true
    }

    fun openEditDialog(note: Note?) {
        noteToEdit = note // Для редактирования
        // При редактировании тип не важен, т.к. время уже задано/отсутствует
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
        topBar = {
            TopAppBar(
                title = { Text(displayDate) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        // ⭐️ НОВЫЙ FAB КОМПОНЕНТ
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
            // ⭐️ ПЕРЕДАЕМ isTask В ДИАЛОГ
            val isTask = noteToEdit?.startTime != null || pendingNoteType == NoteType.TASK

            AddNoteDialog(
                noteToEdit = noteToEdit,
                isTask = isTask,
                defaultDay = selectedDay,
                onDismiss = { showDialog = false; noteToEdit = null; pendingNoteType = null },
                onSaveNote = { id, day, content, startTime ->
                    noteViewModel.saveNote(id, day, content, startTime)
                    showDialog = false
                    noteToEdit = null
                    pendingNoteType = null
                }
            )
        }
    }
}

// ******************************************************
// КОМПОНЕНТ FAB CONTAINER - ДВЕ КНОПКИ
// ******************************************************

@Composable
fun FabContainer(
    onAddTask: () -> Unit,
    onAddNote: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp)
    ) {
        // Кнопка для Добавления ЗАМЕТКИ (без времени)
        ExtendedFloatingActionButton(
            onClick = onAddNote,
            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
            text = { Text("Заметка") },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )

        // Кнопка для Добавления ДЕЛА (со временем)
        ExtendedFloatingActionButton(
            onClick = onAddTask,
            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            text = { Text("Дело") },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}


// ******************************************************
// AddNoteDialog - УСЛОВНОЕ ОТОБРАЖЕНИЕ ВРЕМЕНИ
// ******************************************************

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    noteToEdit: Note?,
    isTask: Boolean, // ⭐️ НОВЫЙ ПАРАМЕТР: Является ли это Делом (со временем)
    defaultDay: String,
    onDismiss: () -> Unit,
    onSaveNote: (id: Int, day: String, content: String, startTime: LocalTime?) -> Unit
) {
    val isEditing = noteToEdit != null
    val selectedDay = defaultDay

    val initialContent = noteToEdit?.content ?: ""
    val noteId = noteToEdit?.id ?: 0

    val initialTime = if (isTask || isEditing) noteToEdit?.startTime else null
    var noteContent by remember { mutableStateOf(initialContent) }
    var selectedTime by remember { mutableStateOf(initialTime) } // LocalTime?
    // Автооткрытие только для новых Дел (isTask=true) и если время еще не установлено
    var showTimePicker by remember { mutableStateOf(isTask && noteToEdit?.startTime == null) }

    val dialogTitle = when {
        isEditing -> "Редактировать ${if (isTask) "Дело" else "Заметку"}"
        isTask -> "Добавить Дело (со временем)"
        else -> "Добавить Заметку (без времени)"
    }

    val displayDate = try {
        val date = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
        val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))
        val dateDisplay = date.format(DATE_FORMAT_DISPLAY)
        "$dayName, $dateDisplay"
    } catch (e: Exception) {
        selectedDay
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle) },
        text = {
            Column {
                Text(
                    text = displayDate,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // ⭐️ УСЛОВНЫЙ UI ДЛЯ ВЫБОРА ВРЕМЕНИ (ТОЛЬКО ДЛЯ ДЕЛ)
                if (isTask) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Время дела:", modifier = Modifier.weight(1f))

                        // Кнопка для открытия TimePicker
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Выбрать время")
                        }

                        // Кнопка для сброса времени (если оно было выбрано)
                        if (selectedTime != null) {
                            IconButton(onClick = { selectedTime = null }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Удалить время")
                            }
                        }
                    }
                }


                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Текст ${if (isTask) "дела" else "заметки"}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteContent.isNotBlank()) {
                        // Для Дела (Task) передаем selectedTime, для Заметки (Note) всегда null
                        val finalTime = if (isTask) selectedTime else null
                        onSaveNote(noteId, selectedDay, noteContent.trim(), finalTime)
                    }
                },
                // ⭐️ ОБНОВЛЕННАЯ ЛОГИКА АКТИВНОСТИ:
                // Кнопка активна, если:
                // 1. Есть текст.
                // 2. ИЛИ это не Дело (isTask=false)
                // 3. ИЛИ это Дело (isTask=true), но время выбрано (selectedTime != null).
                enabled = noteContent.isNotBlank() && (!isTask || selectedTime != null)
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

    // Time Picker Dialog
    if (showTimePicker) {
        val now = LocalTime.now()
        // Используем текущее время, если оно не было выбрано ранее
        val initialHour = selectedTime?.hour ?: now.hour
        val initialMinute = selectedTime?.minute ?: now.minute

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        // Диалоговое окно для TimePicker
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    // Устанавливаем время и закрываем
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                }) {
                    Text("Отмена")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}

// ******************************************************
// ОБНОВЛЕННЫЕ КОМПОНЕНТЫ
// ******************************************************

// NoteList:
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NoteList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onDeleteNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit,
    onToggleDone: (Note) -> Unit
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        // Сортировка по isDone, затем по startTime
        items(notes.sortedWith(
            compareBy<Note> { it.isDone }
                .thenBy { it.startTime == null } // Сначала Дела (со временем), затем Заметки (без времени)
                .thenBy { it.startTime }
        ), key = { it.id }) { note ->
            NoteItem(
                note = note,
                onEdit = { onEditNote(note) },
                onDelete = { onDeleteNote(note) },
                onToggleDone = { onToggleDone(note) }
            )
            HorizontalDivider()
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

// NoteItem: УЛУЧШЕННОЕ ОТОБРАЖЕНИЕ
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit, onToggleDone: () -> Unit) {
    val cardAlpha = if (note.isDone) 0.6f else 1.0f
    val textDecoration = if (note.isDone) TextDecoration.LineThrough else null

    val timeDisplay = note.startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
    val isTask = timeDisplay != null
    val containerColor = if (isTask) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .alpha(cardAlpha)
            .combinedClickable(onClick = onEdit),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ⭐️ УСЛОВНОЕ ОТОБРАЖЕНИЕ: Чекбокс только для Дел
            if (isTask) {
                Checkbox(
                    checked = note.isDone,
                    onCheckedChange = { onToggleDone() },
                    modifier = Modifier.padding(end = 8.dp)
                )
            } else {
                Spacer(modifier = Modifier.width(32.dp)) // Визуальный отступ для выравнивания
            }

            Column(modifier = Modifier.weight(1f)) {
                if (isTask) {
                    // ⭐️ НОВЫЙ ФОРМАТ: "ВРЕМЯ - ДЕЛО"
                    Text(
                        text = "$timeDisplay - ${note.content}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        textDecoration = textDecoration,
                        maxLines = 2 // Ограничиваем, чтобы избежать слишком большого текста
                    )
                } else {
                    // Основное содержимое для Заметки
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить заметку")
            }
        }
    }
}
