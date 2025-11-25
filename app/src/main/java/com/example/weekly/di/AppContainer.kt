package com.example.weekly.di

import android.content.Context
import com.example.weekly.Data.Local.NoteDatabase
import com.example.weekly.Data.Repository.NoteRepositoryImpl
import com.example.weekly.Data.Repository.SettingsRepositoryImpl
import com.example.weekly.Data.Settings.SettingsManager
import com.example.weekly.Data.dataStore
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Presentation.ViewModel.NoteViewModelFactory

/**
 * Контейнер зависимостей приложения (Manual Dependency Injection).
 * Здесь создаются и хранятся все синглтоны (Repository, UseCases, Factory).
 */
class AppContainer(context: Context) {

    // 1. Database & Settings (Data Layer)
    private val database = NoteDatabase.getDatabase(context)
    val settingsManager =
        SettingsManager(context.dataStore) // dataStore должен быть доступен через Context extension

    // 2. Repository (Data Layer)
    private val noteRepository = NoteRepositoryImpl(database.noteDao())
    private val settingsRepository = SettingsRepositoryImpl(settingsManager)

    // 3. UseCases (Domain Layer)
    val getOrderedNotesUseCase = GetOrderedNotesUseCase(noteRepository)
    val deleteUseCase = DeleteUseCase(noteRepository)
    val saveNoteUseCase = SaveNoteUseCase(noteRepository)
    val toggleDoneStatusUseCase = ToggleDoneStatusUseCase(noteRepository)

    val getThemeUseCase = GetThemeUseCase(settingsRepository)

    val toggleThemeUseCase = ToggleThemeUseCase(settingsRepository)

    // 4. ViewModel Factory (Presentation Layer)
    val noteViewModelFactory = NoteViewModelFactory(
        getOrderedNotesUseCase = getOrderedNotesUseCase,
        deleteUseCase = deleteUseCase,
        saveNoteUseCase = saveNoteUseCase,
        toggleDoneStatusUseCase = toggleDoneStatusUseCase,
        getThemeUseCase = getThemeUseCase,
        toggleThemeUseCase = toggleThemeUseCase
    )
}
