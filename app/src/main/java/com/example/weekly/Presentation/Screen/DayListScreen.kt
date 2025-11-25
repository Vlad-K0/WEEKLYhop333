package com.example.weekly.Presentation.Screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Presentation.ViewModel.NoteViewModel
import com.example.weekly.Presentation.DATE_FORMAT_DISPLAY
import com.example.weekly.Presentation.DATE_FORMAT_ISO
import com.example.weekly.Presentation.LOCALE_RU
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayListScreen(
    onDayClick: (String) -> Unit,                 // функция, вызываемая при клике на день
    noteViewModel: NoteViewModel                 // ViewModel для работы с данными и темой
) {
    // 1. Подписываемся на ЕДИНЫЙ стейт
    val state by noteViewModel.uiState.collectAsState()

    // вычисляем конец недели и создаём строку диапазона дат
    val weekEnd = state.currentWeekStart.plusDays(6)
    val weekRange = "${state.currentWeekStart.format(DATE_FORMAT_DISPLAY)} – ${weekEnd.format(
        DATE_FORMAT_DISPLAY
    )}"

    //сегодняшняя дата (для подсветки)
    val today = LocalDate.now()

    // основная структура экрана —
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            // 🔝 Верхняя панель приложения
            TopAppBar(
                title = {
                    Text(
                        text = "\uD83D\uDCDD weekly", // Заголовок приложения
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    // переключатель темы (Светлая / Тёмная)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = if (state.isDarkTheme) "Тёмная" else "Светлая", // Надпись рядом со Switch
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(Modifier.width(8.dp))
                        Switch(
                            checked = state.isDarkTheme,
                            onCheckedChange = { noteViewModel.toggleTheme(it) } // Меняем тему через ViewModel
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        }
    ) { paddingValues ->
        //основной контент экрана
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            // панель для переключения недель (стрелки ← и →)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //предыдущая неделя
                IconButton(onClick = { noteViewModel.onPreviousWeekClick() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Предыдущая неделя")
                }

                //диапазон дат недели (например: 04.11 – 10.11)
                Text(
                    text = weekRange,
                    style = MaterialTheme.typography.titleLarge
                )

                //следующая неделя
                IconButton(onClick = { noteViewModel.onNextWeekClick() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Следующая неделя")
                }
            }

            //разделительная линия
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)

            // список всех дней текущей недели
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.weekDates) { date ->

                    // форматируем дату в ISO и получаем имя дня недели
                    val dateStringISO = date.format(DATE_FORMAT_ISO)
                    val dayName = date.format(DateTimeFormatter.ofPattern("EEEE", LOCALE_RU))

                    //проверяем, совпадает ли с сегодняшним днём
                    val isToday = date.isEqual(today)

                    //получаем заметки для текущего дня (если нет — пустой список)
                    val notes = state.notes[dateStringISO] ?: emptyList()

                    // формируем короткий текст
                    val noteSnippet = notes.sortedWith(
                        compareBy<Note> { it.isDone }
                            .thenBy { it.startTime }
                    ).firstOrNull { !it.isDone }?.content
                        ?: notes.firstOrNull()?.content
                        ?: "Нет запланированных дел." // если совсем пусто

                    // карточка одного дня недели
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .clickable { onDayClick(dateStringISO) }, // При клике — переход на экран дня
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                //если сегодня — подсвечиваем карточку
                                isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f)
                                else -> MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        // внутреннее содержимое карточки
                        Column(modifier = Modifier.padding(20.dp)) {
                            //верхняя строка: название дня и дата
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dayName, //название дня
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = date.format(DATE_FORMAT_DISPLAY), // форматированная дата
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // короткое описание заметки
                            Text(
                                text = noteSnippet,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
}

