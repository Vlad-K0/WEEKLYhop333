package com.example.weekly.Presentation.Components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.weekly.Domain.Model.Note
import java.time.format.DateTimeFormatter

// NoteList:
@RequiresApi(Build.VERSION_CODES.O)
@Composable

//отображение списка заметок
fun NoteList(
    modifier: Modifier = Modifier,
    notes: List<Note>,
    onDeleteNote: (Note) -> Unit,
    onEditNote: (Note) -> Unit,
    onToggleDone: (Note) -> Unit
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 8.dp)
    ) {
        items(notes.sortedWith(
            //сортировка заметок перед отображением
            compareBy<Note> { it.isDone }
                .thenBy { it.startTime == null }
                .thenBy { it.startTime }
        ), key = { it.id }) { note ->
            NoteItem(
                note = note,
                onEdit = { onEditNote(note) },
                onDelete = { onDeleteNote(note) },
                onToggleDone = { onToggleDone(note) }
            )
        }
    }
    if (notes.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize().padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("В этот день пока нет заметок.", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

// NoteItem:
@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalFoundationApi::class)
@Composable

//отображение отдельной заметки
fun NoteItem(note: Note, onEdit: () -> Unit, onDelete: () -> Unit, onToggleDone: () -> Unit) {
    //если выполнена, то зачеркиваем
    val cardAlpha = if (note.isDone) 0.6f else 1.0f
    val textDecoration = if (note.isDone) TextDecoration.LineThrough else null

    //определение типа заметки
    val timeDisplay = note.startTime?.format(DateTimeFormatter.ofPattern("HH:mm"))
    val isTask = timeDisplay != null

    //задает цвет в зависимости от типа заметки
    val containerColor = if (isTask) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.secondaryContainer
    val contentColor = if (isTask) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSecondaryContainer
    val iconColor = if (isTask) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .alpha(cardAlpha)
            .combinedClickable(
                onClick = onEdit,
                onLongClick = onToggleDone //переключение статуса по долгому нажатию
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (isTask) {
                Checkbox(
                    checked = note.isDone,
                    onCheckedChange = { onToggleDone() },
                    modifier = Modifier.padding(end = 8.dp).align(Alignment.CenterVertically),
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.6f)
                    )
                )
            } else {
                Icon(
                    Icons.Filled.Menu,
                    contentDescription = "Заметка",
                    modifier = Modifier.padding(end = 12.dp).size(24.dp).align(Alignment.CenterVertically),
                    tint = iconColor
                )
            }

            //контент
            Column(modifier = Modifier.weight(1f)) {
                val contentText = if (isTask) "$timeDisplay - ${note.content}" else note.content

                Text(
                    text = contentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = contentColor,
                    textDecoration = textDecoration
                )
            }

            //кнопка удаления
            IconButton(onClick = onDelete) {
                Icon(Icons.Filled.Delete, contentDescription = "Удалить заметку", tint = contentColor.copy(alpha = 0.7f))
            }
        }
    }
}