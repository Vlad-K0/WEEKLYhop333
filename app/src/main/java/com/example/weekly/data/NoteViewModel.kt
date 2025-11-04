package com.example.weekly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalTime

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // ⭐️ ИСПРАВЛЕНА ЛОГИКА ГРУППИРОВКИ И СОРТИРОВКИ
    val notesGroupedByDay: StateFlow<Map<String, List<Note>>> = repository.getAllNotes()
        .map { notes: List<Note> -> // Явно указываем тип List<Note>
            notes
                .groupBy { it.day } // Группировка по полю 'day'
                .mapValues { (_, dayNotes: List<Note>) -> // Явно указываем тип List<Note>
                    // Сортировка:
                    dayNotes.sortedWith(
                        compareBy<Note> { it.isDone } // 1. Сначала невыполненные, затем выполненные
                            .thenBy { it.startTime == null } // 2. Сначала Дела (со временем), затем Заметки (без времени)
                            .thenBy { it.startTime } // 3. Дела сортируем по времени начала
                    )
                }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun deleteNote(note: Note) = viewModelScope.launch {
        repository.deleteNote(note)
    }

    fun toggleDoneStatus(note: Note) = viewModelScope.launch {
        repository.toggleDoneStatus(note)
    }

    // ⭐️ ОБНОВЛЕН: Принимает LocalTime?
    fun saveNote(id: Int, day: String, content: String, startTime: LocalTime?) = viewModelScope.launch {
        // Блокируем, чтобы получить isDone, прежде чем обновить заметку.
        // Поскольку getNoteById может быть suspend, лучше использовать с withContext(Dispatchers.IO)
        // или использовать обертку, как сделано здесь (если ваш DAO не suspend).

        // ВАЖНО: getNoteById должен быть выполнен в потоке, отличном от Main
        val existingNote = if (id != 0) repository.getNoteById(id) else null

        val note = Note(
            id = if (id == 0) 0 else id,
            day = day,
            content = content,
            // Сохраняем isDone для существующей заметки, если она была
            isDone = existingNote?.isDone ?: false,
            startTime = startTime
        )
        // ⭐️ ИСПОЛЬЗУЕМ upsertNote
        repository.upsertNote(note)
    }
}

// ******************************************************
// FACTORY
// ******************************************************

class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
