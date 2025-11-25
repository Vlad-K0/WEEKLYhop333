package com.example.weekly.Presentation.Components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.weekly.Domain.Model.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupManagementDialog(
    groups: List<Group>,
    onDismiss: () -> Unit,
    onAddGroup: (name: String, color: String) -> Unit,
    onDeleteGroup: (groupId: Int) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Управление группами") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Список существующих групп
                if (groups.isEmpty()) {
                    Text(
                        "Нет групп",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 300.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(groups) { group ->
                            Card(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Цветной индикатор
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(
                                                Color(android.graphics.Color.parseColor(group.color)),
                                                CircleShape
                                            )
                                    )
                                    
                                    Spacer(Modifier.width(12.dp))
                                    
                                    // Название группы
                                    Text(
                                        text = group.name,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    // Кнопка удаления
                                    IconButton(
                                        onClick = { onDeleteGroup(group.id) }
                                    ) {
                                        Icon(
                                            Icons.Default.Delete,
                                            contentDescription = "Удалить группу",
                                            tint = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                Spacer(Modifier.height(16.dp))
                
                // Кнопка добавления новой группы
                Button(
                    onClick = { showAddDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Добавить группу")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
    
    // Диалог добавления новой группы
    if (showAddDialog) {
        AddGroupDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, color ->
                onAddGroup(name, color)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGroupDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, color: String) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf("#FF5722") }
    
    // Предустановленные цвета
    val predefinedColors = listOf(
        "#FF5722" to "Красный",
        "#E91E63" to "Розовый",
        "#9C27B0" to "Фиолетовый",
        "#673AB7" to "Темно-фиолетовый",
        "#3F51B5" to "Индиго",
        "#2196F3" to "Синий",
        "#03A9F4" to "Голубой",
        "#00BCD4" to "Циан",
        "#009688" to "Бирюзовый",
        "#4CAF50" to "Зеленый",
        "#8BC34A" to "Светло-зеленый",
        "#CDDC39" to "Лайм",
        "#FFEB3B" to "Желтый",
        "#FFC107" to "Янтарный",
        "#FF9800" to "Оранжевый",
        "#FF5722" to "Красно-оранжевый"
    )
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая группа") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Поле ввода названия
                OutlinedTextField(
                    value = groupName,
                    onValueChange = { groupName = it },
                    label = { Text("Название группы") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                
                Spacer(Modifier.height(16.dp))
                
                Text(
                    "Выберите цвет:",
                    style = MaterialTheme.typography.bodyMedium
                )
                
                Spacer(Modifier.height(8.dp))
                
                // Сетка цветов
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    predefinedColors.chunked(4).forEach { rowColors ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowColors.forEach { (color, _) ->
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .background(
                                            Color(android.graphics.Color.parseColor(color)),
                                            CircleShape
                                        )
                                        .then(
                                            if (selectedColor == color) {
                                                Modifier.padding(4.dp)
                                            } else Modifier
                                        )
                                        .background(
                                            if (selectedColor == color) {
                                                MaterialTheme.colorScheme.outline
                                            } else Color.Transparent,
                                            CircleShape
                                        )
                                        .padding(if (selectedColor == color) 4.dp else 0.dp)
                                        .background(
                                            Color(android.graphics.Color.parseColor(color)),
                                            CircleShape
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    IconButton(
                                        onClick = { selectedColor = color },
                                        modifier = Modifier.size(48.dp)
                                    ) {}
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(groupName, selectedColor) },
                enabled = groupName.isNotBlank()
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
