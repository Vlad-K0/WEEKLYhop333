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
    noteToEdit: Note?, //если не null — редактируем существующую заметку
    isTask: Boolean, //тип создаваемого элемента (дело или обычная заметка)
    defaultDay: String, //день, к которому будет добавлена заметка
    onDismiss: () -> Unit, // акрытие диалога
    onSaveNote: (id: Int, day: String, content: String, startTime: LocalTime?) -> Unit // callback при сохранении
) {
    val isEditing = noteToEdit != null
    val noteId = noteToEdit?.id ?: 0 //если редактируем — сохраняем id

    //текущий выбранный день (по умолчанию — выбранный в календаре)
    var selectedDay by remember { mutableStateOf(noteToEdit?.day ?: defaultDay) }

    //начальный текст заметки
    val initialContent = noteToEdit?.content ?: ""
    var noteContent by remember { mutableStateOf(initialContent) }

    //время начала (если это "дело")
    val initialTime = if (isTask || isEditing) noteToEdit?.startTime else null
    var selectedTime by remember { mutableStateOf(initialTime) }

    //флаг для показа диалога выбора времени
    var showTimePicker by remember { mutableStateOf(isTask && noteToEdit?.startTime == null) }

    //флаг для показа календаря
    var showDatePicker by remember { mutableStateOf(false) }

    //заголовок окна
    val dialogTitle = when {
        isEditing -> "Редактировать ${if (isTask) "Дело" else "Заметку"}"
        isTask -> "Добавить Дело (со временем)"
        else -> "Добавить Заметку (без времени)"
    }

    //форматированная дата для отображения в UI
    val displayDate: String = remember(selectedDay) {
        try {
            val date = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
            val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))
            val dateDisplay = date.format(DATE_FORMAT_DISPLAY)
            //с большой буквы день недели
            val capitalizedDayName = dayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(LOCALE_RU) else it.toString() }
            "$capitalizedDayName, $dateDisplay"
        } catch (e: Exception) {
            selectedDay // если парсинг не удался
        }
    }

    // основное диалоговое окно
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(dialogTitle, style = MaterialTheme.typography.titleMedium) },
        text = {
            Column {
                // строка выбора даты
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Дата:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.weight(1f)
                    )

                    //кнопка для открытия календаря
                    TextButton(onClick = { showDatePicker = true }) {
                        Text(displayDate, style = MaterialTheme.typography.labelLarge)
                    }
                }

                // если это "дело" (с временем)
                if (isTask) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Время дела:", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)

                        //кнопка для выбора времени
                        TextButton(onClick = { showTimePicker = true }) {
                            Text(
                                selectedTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
                                    ?: "Выбрать время",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }

                        //кнопка очистки времени (иконка мусорки)
                        if (selectedTime != null) {
                            IconButton(onClick = { selectedTime = null }) {
                                Icon(Icons.Filled.Delete, contentDescription = "Удалить время")
                            }
                        }
                    }
                }

                // поле ввода текста заметки
                OutlinedTextField(
                    value = noteContent,
                    onValueChange = { noteContent = it },
                    label = { Text("Текст ${if (isTask) "дела" else "заметки"}") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        // кнопка "Сохранить" или "Добавить"
        confirmButton = {
            TextButton(
                onClick = {
                    if (noteContent.isNotBlank()) {
                        val finalTime = if (isTask) selectedTime else null
                        onSaveNote(noteId, selectedDay, noteContent.trim(), finalTime)
                    }
                },
                // Кнопка активна только если текст введён (и время выбрано, если это дело)
                enabled = noteContent.isNotBlank() && (!isTask || selectedTime != null)
            ) {
                Text(if (isEditing) "Сохранить" else "Добавить", style = MaterialTheme.typography.labelLarge)
            }
        },

        // кнопка "Отмена"
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", style = MaterialTheme.typography.labelLarge)
            }
        }
    )

    //диалог выбора даты
    if (showDatePicker) {
        val initialDate = LocalDate.parse(selectedDay, DATE_FORMAT_ISO)
        val initialSelectedDateMillis = initialDate
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val dateState = rememberDatePickerState(
            initialSelectedDateMillis = initialSelectedDateMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val newDateMillis = dateState.selectedDateMillis
                    if (newDateMillis != null) {
                        val newLocalDate = Instant.ofEpochMilli(newDateMillis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                        selectedDay = newLocalDate.format(DATE_FORMAT_ISO)
                    }
                    showDatePicker = false
                }) {
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Отмена")
                }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    //диалог выбора времени
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
                    Text("ОК")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Отмена")
                }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
