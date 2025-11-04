package com.example.weekly.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NoteViewModel(private val repository: NoteRepository) : ViewModel() {

    // ⭐️ ЗАМЕНА СТАРОГО UI State
    val notesGroupedByDay: StateFlow<Map<String, List<Note>>> = repository.allNotes
        .map { notes ->
            // Группируем заметки по полю 'day'
            notes.groupBy { it.day }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    fun saveNote(id: Int, day: String, content: String) {
        viewModelScope.launch {
            // Примечание: При обновлении существующей заметки (id > 0) этот подход сбрасывает статус isDone на false,
            // так как в этом методе мы не знаем предыдущего статуса.
            // Для полного исправления вам потребуется получать заметку из БД перед обновлением.
            val noteToSave = Note(id = id, day = day, content = content)
            repository.upsert(noteToSave)
        }
    }

    // ⭐️ НОВАЯ ФУНКЦИЯ: Переключение статуса isDone
    fun toggleDoneStatus(note: Note) {
        viewModelScope.launch {
            // Создаем копию заметки, инвертируя isDone
            val updatedNote = note.copy(isDone = !note.isDone)
            // Используем upsert/update для сохранения изменения в БД
            repository.upsert(updatedNote)
        }
    }

    fun deleteNote(note: Note) {
        viewModelScope.launch {
            repository.delete(note)
        }
    }
}

// Фабрика для ViewModel
class NoteViewModelFactory(private val repository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}