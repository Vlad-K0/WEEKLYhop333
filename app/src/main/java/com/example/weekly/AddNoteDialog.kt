package com.example.weekly

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weekly.data.Note
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteDialog(
    noteToEdit: Note?,
    isTask: Boolean,
    defaultDay: String,
    onDismiss: () -> Unit,
    onSaveNote: (id: Int, day: String, content: String, startTime: LocalTime?) -> Unit
) {
    val isEditing = noteToEdit != null
    val noteId = noteToEdit?.id ?: 0

    // ⭐️ Состояние для выбранной даты
    var selectedDay by remember {
        mutableStateOf(noteToEdit?.day ?: defaultDay)
    }
    val initialContent = noteToEdit?.content ?: ""

    val initialTime = if (isTask || isEditing) noteToEdit?.startTime else null
    var noteContent by remember { mutableStateOf(initialContent) }
    var selectedTime by remember { mutableStateOf(initialTime) }
    var showTimePicker by remember { mutableStateOf(isTask && noteToEdit?.startTime == null) }

    // ⭐️ Состояние для отображения календаря
    var showDatePicker by remember { mutableStateOf(false) }

    val dialogTitle = when {
        isEditing -> "Редактировать ${if (isTask) "Дело" else "Заметку"}"
        isTask -> "Добавить Дело (со временем)"
        else -> "Добавить Заметку (без времени)"
    }

    // ⭐️ Динамическое форматирование даты
    val displayDate: String = remember(selectedDay) {
        try {
            val date = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
            val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))
            val dateDisplay = date.format(DATE_FORMAT_DISPLAY)
            // Капитализация первого символа дня недели
            val capitalizedDayName = dayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LOCALE_RU) else it.toString() }
            "$capitalizedDayName, $dateDisplay"
        } catch (e: Exception) {
            selectedDay
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                // ⭐️ Выбор даты
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Дата:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    TextButton(onClick = { showDatePicker = true }) {
                        Text(displayDate, style = MaterialTheme.typography.labelLarge)
                    }
                }

                if (isTask) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Время дела:", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)

                        TextButton(onClick = { showTimePicker = true }) {
                            Text(selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "Выбрать время", style = MaterialTheme.typography.labelLarge)
                        }

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
                    label = { Text("Текст ${if (isTask) "дела" else "заметки"}", style = MaterialTheme.typography.bodyMedium) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteContent.isNotBlank()) {
                        val finalTime = if (isTask) selectedTime else null
                        onSaveNote(noteId, selectedDay, noteContent.trim(), finalTime)
                    }
                },
                enabled = noteContent.isNotBlank() && (!isTask || selectedTime != null)
            ) {
                Text(if (isEditing) "Сохранить" else "Добавить", style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", style = MaterialTheme.typography.labelLarge)
            }
        }
    )

    // ⭐️ ДИАЛОГ ВЫБОРА ДАТЫ
    if (showDatePicker) {
        val initialDate = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
        val initialSelectedDateMillis = initialDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newDateMillis = dateState.selectedDateMillis
                    if (newDateMillis != null) {
                        val newLocalDate = Instant.ofEpochMilli(newDateMillis).atZone(ZoneId.systemDefault()).toLocalDate()
                        selectedDay = newLocalDate.format(DATE_FORMAT_ISO)
                    }
                    showDatePicker = false
                }) {
                    Text("ОК", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена", style = MaterialTheme.typography.labelLarge)
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    if (showTimePicker) {
        val now = LocalTime.now()
        val initialHour = selectedTime?.hour ?: now.hour
        val initialMinute = selectedTime?.minute ?: now.minute

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    selectedTime = LocalTime.of(timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) {
                    Text("ОК", style = MaterialTheme.typography.labelLarge)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTimePicker = false
                }) {
                    Text("Отмена", style = MaterialTheme.typography.labelLarge)
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}