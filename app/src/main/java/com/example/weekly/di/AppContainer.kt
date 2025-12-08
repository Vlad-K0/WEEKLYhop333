package com.example.weekly.di

import android.content.Context
import com.example.weekly.Data.Local.NoteDatabase
import com.example.weekly.Data.Notification.AlarmScheduler
import com.example.weekly.Data.Notification.NotificationHelper
import com.example.weekly.Data.Remote.IsDayOffApiService
import com.example.weekly.Data.Repository.GroupRepositoryImpl
import com.example.weekly.Data.Repository.HolidayRepositoryImpl
import com.example.weekly.Data.Repository.NoteRepositoryImpl
import com.example.weekly.Data.Repository.NotificationRepositoryImpl
import com.example.weekly.Data.Repository.SettingsRepositoryImpl
import com.example.weekly.Data.Settings.SettingsManager
import com.example.weekly.Data.dataStore
import com.example.weekly.Domain.Usecase.GroupUseCases.DeleteGroupUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.GetAllGroupsUseCase
import com.example.weekly.Domain.Usecase.GroupUseCases.SaveGroupUseCase
import com.example.weekly.Domain.Usecase.HolidayUseCases.GetHolidaysForWeekUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.DeleteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.GetOrderedNotesUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.SaveNoteUseCase
import com.example.weekly.Domain.Usecase.NoteUseCases.ToggleDoneStatusUseCase
import com.example.weekly.Domain.Usecase.NotificationUseCases.CancelNotificationUseCase
import com.example.weekly.Domain.Usecase.NotificationUseCases.ScheduleNotificationUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.GetThemeUseCase
import com.example.weekly.Domain.Usecase.ThemeUseCase.ToggleThemeUseCase
import com.example.weekly.Presentation.ViewModel.NoteViewModelFactory

/**
 * Контейнер зависимостей приложения (Manual Dependency Injection).
 */
class AppContainer(context: Context) {

    // 1. Database & Settings (Data Layer)
    private val database = NoteDatabase.getDatabase(context)
    val settingsManager = SettingsManager(context.dataStore)

    // 2. Notification & API Infrastructure
    private val alarmScheduler = AlarmScheduler(context)
    private val isDayOffApiService = IsDayOffApiService()

    // 3. Repositories (Data Layer)
    private val noteRepository = NoteRepositoryImpl(database.noteDao())
    private val groupRepository = GroupRepositoryImpl(database.groupDao())
    private val settingsRepository = SettingsRepositoryImpl(settingsManager)
    private val notificationRepository = NotificationRepositoryImpl(alarmScheduler)
    private val holidayRepository = HolidayRepositoryImpl(isDayOffApiService)

    // 4. UseCases (Domain Layer) - Заметки
    val getOrderedNotesUseCase = GetOrderedNotesUseCase(noteRepository)
    val deleteUseCase = DeleteUseCase(noteRepository)
    val saveNoteUseCase = SaveNoteUseCase(noteRepository)
    val toggleDoneStatusUseCase = ToggleDoneStatusUseCase(noteRepository)

    // UseCases - Группы
    val getAllGroupsUseCase = GetAllGroupsUseCase(groupRepository)
    val saveGroupUseCase = SaveGroupUseCase(groupRepository)
    val deleteGroupUseCase = DeleteGroupUseCase(groupRepository)

    // UseCases - Тема
    val getThemeUseCase = GetThemeUseCase(settingsRepository)
    val toggleThemeUseCase = ToggleThemeUseCase(settingsRepository)

    // UseCases - Уведомления
    val scheduleNotificationUseCase = ScheduleNotificationUseCase(notificationRepository)
    val cancelNotificationUseCase = CancelNotificationUseCase(notificationRepository)

    // UseCases - Праздники
    val getHolidaysForWeekUseCase = GetHolidaysForWeekUseCase(holidayRepository)

    // 5. ViewModel Factory (Presentation Layer)
    val noteViewModelFactory = NoteViewModelFactory(
        getOrderedNotesUseCase = getOrderedNotesUseCase,
        deleteUseCase = deleteUseCase,
        saveNoteUseCase = saveNoteUseCase,
        toggleDoneStatusUseCase = toggleDoneStatusUseCase,
        getThemeUseCase = getThemeUseCase,
        toggleThemeUseCase = toggleThemeUseCase,
        getAllGroupsUseCase = getAllGroupsUseCase,
        saveGroupUseCase = saveGroupUseCase,
        deleteGroupUseCase = deleteGroupUseCase,
        scheduleNotificationUseCase = scheduleNotificationUseCase,
        cancelNotificationUseCase = cancelNotificationUseCase,
        getHolidaysForWeekUseCase = getHolidaysForWeekUseCase
    )
}
