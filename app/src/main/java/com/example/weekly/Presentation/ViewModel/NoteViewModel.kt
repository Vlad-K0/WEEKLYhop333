package com.example.weekly.Presentation.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weekly.Domain.Usecase.GroupUseCases.DeleteGroupUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.GetAllGroupsUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.SaveGroupUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase
import com.example.weekly.Domain.Usecase.NotificationUseCases.CancelNotificationUseCase
import com.example.weekly.Domain.Usecase.NotificationUseCases.ScheduleNotificationUseCase
import com.example.weekly.Presentation.State.DayListUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.TemporalAdjusters

class NoteViewModel(
    // Заметки
    private val getOrderedNotesUseCase: GetOrderedNotesUseCase,
    private val deleteUseCase: DeleteUseCase,
    private val saveNoteUseCase: SaveNoteUseCase,
    private val toggleDoneStatusUseCase: ToggleDoneStatusUseCase,

    // Тема
    private val getThemeUseCase: GetThemeUseCase,
    private val toggleThemeUseCase: ToggleThemeUseCase,

    // Группы
    private val getAllGroupsUseCase: GetAllGroupsUseCase,
    private val saveGroupUseCase: SaveGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase,

    // Уведомления
    private val scheduleNotificationUseCase: ScheduleNotificationUseCase,
    private val cancelNotificationUseCase: CancelNotificationUseCase
) : ViewModel() {

    // Внутренний MutableStateFlow
    private val _uiState = MutableStateFlow(DayListUiState())
    
    // Публичный StateFlow для UI
    val uiState: StateFlow<DayListUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            // Объединяем три потока: заметки, тема, и группы
            combine(
                getOrderedNotesUseCase(),
                getThemeUseCase(),
                getAllGroupsUseCase()
            ) { allNotes, isDark, groups ->
                // Фильтруем заметки по выбранной группе
                val filteredNotes = if (_uiState.value.selectedGroupId == null) {
                    allNotes // Показываем все
                } else {
                    allNotes.mapValues { (_, notes) ->
                        notes.filter { it.groupId == _uiState.value.selectedGroupId }
                    }.filterValues { it.isNotEmpty() }
                }
                
                _uiState.value.copy(
                    notes = filteredNotes,
                    isDarkTheme = isDark,
                    groups = groups,
                    isLoading = false
                )
            }.collect { newState ->
                _uiState.update { newState }
            }
        }
        
        updateWeekDates(LocalDate.now())
    }

    // Логика переключения недель
    fun onNextWeekClick() {
        val newStart = _uiState.value.currentWeekStart.plusWeeks(1)
        updateWeekDates(newStart)
    }

    fun onPreviousWeekClick() {
        val newStart = _uiState.value.currentWeekStart.minusWeeks(1)
        updateWeekDates(newStart)
    }

    private fun updateWeekDates(startDate: LocalDate) {
        val startOfWeek = startDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        val days = (0L..6L).map { startOfWeek.plusDays(it) }
        
        _uiState.update { it.copy(
            currentWeekStart = startOfWeek,
            weekDates = days
        )}
    }

    // Выбор группы для фильтрации
    fun onGroupSelected(groupId: Int?) {
        _uiState.update { it.copy(selectedGroupId = groupId) }
        // Перезагружаем данные с новым фильтром
        loadInitialData()
    }

    // Функция для переключения темы
    fun toggleTheme(isDark: Boolean) {
        viewModelScope.launch {
            toggleThemeUseCase(isDark)
        }
    }

    // Удаление заметки
    fun deleteNote(note: Note) = viewModelScope.launch {
        // Отменяем уведомление при удалении
        cancelNotificationUseCase(note.id)
        deleteUseCase(note)
    }

    // Переключение статуса "выполнено/не выполнено"
    fun toggleDoneStatus(note: Note) = viewModelScope.launch {
        // Если задача выполнена - отменяем уведомление
        if (!note.isDone) {
            cancelNotificationUseCase(note.id)
        }
        toggleDoneStatusUseCase(note)
    }

    // Сохранение новой или редактирование существующей заметки
    fun saveNote(
        id: Int, 
        date: String, 
        content: String, 
        startTime: LocalTime?,
        groupId: Int? = null
    ) = viewModelScope.launch {
        saveNoteUseCase(id, date, content, startTime, groupId)
        
        // Планируем уведомление если есть время
        if (startTime != null) {
            val note = Note(
                id = id,
                content = content,
                date = date,
                isDone = false,
                startTime = startTime,
                groupId = groupId
            )
            scheduleNotificationUseCase(note)
        }
    }
    
    // Сохранение группы
    fun saveGroup(id: Int = 0, name: String, color: String) = viewModelScope.launch {
        saveGroupUseCase(id, name, color)
    }
    
    // Удаление группы
    fun deleteGroup(groupId: Int) = viewModelScope.launch {
        deleteGroupUseCase(groupId)
    }
}
