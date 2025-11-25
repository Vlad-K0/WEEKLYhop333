package com.example.weekly.Presentation.Screen

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
import com.example.weekly.Presentation.Components.AddNoteDialog
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Presentation.Components.NoteList
import com.example.weekly.Presentation.ViewModel.NoteViewModel
import com.example.weekly.Presentation.DATE_FORMAT_DISPLAY
import com.example.weekly.Presentation.DATE_FORMAT_ISO
import com.example.weekly.Presentation.LOCALE_RU
import com.example.weekly.Presentation.NoteType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.LocalTime


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayDetailScreen(
    selectedDay: String,           // дата выбранного дня (в формате ISO, например "2025-11-05")
    noteViewModel: NoteViewModel,  // ViewModel для управления заметками
    onBack: () -> Unit             // функция для возврата назад
) {
    // 1. Подписываемся на ЕДИНЫЙ стейт
    val state by noteViewModel.uiState.collectAsState()

    // 2. Берём список заметок только для выбранного дня
    val dayNotes = state.notes[selectedDay] ?: emptyList()

    //состояния для отображения диалога добавления/редактирования
    var showDialog by remember { mutableStateOf(false) }       //показывать ли диалог
    var noteToEdit: Note? by remember { mutableStateOf(null) } //редактируемая заметка
    var pendingNoteType: NoteType? by remember { mutableStateOf(null) } //тип новой заметки

    // открытие диалога для создания новой заметки
    fun openCreationDialog(type: NoteType?) {
        noteToEdit = null          // создаём новую, не редактируем существующую
        pendingNoteType = type     // сохраняем, какой тип (дело или заметка)
        showDialog = true
    }

    // открытие диалога для редактирования существующей заметки
    fun openEditDialog(note: Note?) {
        noteToEdit = note
        pendingNoteType = null     // тип не нужен — определится по note
        showDialog = true
    }

    //форматируем дату для отображения в заголовке
    val displayDate = try {
        val date = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
        val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))
        val dateDisplay = date.format(DATE_FORMAT_DISPLAY)
        "$dayName, $dateDisplay"
    } catch (e: Exception) {
        selectedDay //fallback, если формат даты некорректный
    }

    //основная структура экрана
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,

        // верхняя панель (TopAppBar)
        topBar = {
            TopAppBar(
                title = { Text(displayDate) }, // Заголовок с датой
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                navigationIcon = {
                    //кнопка "Назад"
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },

        //кнопки добавления (две плавающие кнопки — заметка и дело)
        floatingActionButton = {
            FabContainer(
                onAddTask = { openCreationDialog(NoteType.TASK) }, // кнопка "дело"
                onAddNote = { openCreationDialog(NoteType.NOTE) }  // кнопка "заметка"
            )
        }
    ) { padding ->

        //список заметок для выбранного дня
        NoteList(
            modifier = Modifier.padding(padding),
            notes = dayNotes,
            onDeleteNote = { note -> noteViewModel.deleteNote(note) },        // удалить заметку
            onEditNote = { note -> openEditDialog(note) },                    // открыть диалог редактирования
            onToggleDone = { note -> noteViewModel.toggleDoneStatus(note) }   // переключить статус выполнено/не выполнено
        )

        //диалог добавления / редактирования заметки
        if (showDialog) {
            //определяем, является ли заметка "делом" (с временем начала)
            val isTask = noteToEdit?.startTime != null || pendingNoteType == NoteType.TASK

            AddNoteDialog(
                noteToEdit = noteToEdit,           // если редактируем — передаём заметку
                isTask = isTask,                   // флаг: заметка с временем или без
                defaultDay = selectedDay,          // день, к которому относится заметка
                onDismiss = {
                    //закрытие диалога
                    showDialog = false
                    noteToEdit = null
                    pendingNoteType = null
                },
                // сохранение заметки
                onSaveNote = { id: Int, day: String, content: String, startTime: LocalTime? ->
                    noteViewModel.saveNote(id, day, content, startTime) // вызываем метод ViewModel
                    showDialog = false
                    noteToEdit = null
                    pendingNoteType = null
                }
            )
        }
    }
}



@Composable
fun FabContainer(
    onAddTask: () -> Unit,  // callback при нажатии на "дело"
    onAddNote: () -> Unit   // callback при нажатии на "заметку"
) {
    Column(
        modifier = Modifier.padding(bottom = 16.dp, end = 16.dp),
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(12.dp) // Расстояние между FAB'ами
    ) {
        // кнопка "Заметка"
        ExtendedFloatingActionButton(
            onClick = onAddNote,
            icon = { Icon(Icons.Default.Menu, contentDescription = null) },
            text = { Text("Заметка", style = MaterialTheme.typography.labelLarge) },
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )

        // кнопка "Дело" (с временем)
        ExtendedFloatingActionButton(
            onClick = onAddTask,
            icon = { Icon(Icons.Default.Schedule, contentDescription = null) },
            text = { Text("Дело", style = MaterialTheme.typography.labelLarge) },
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    }
}
