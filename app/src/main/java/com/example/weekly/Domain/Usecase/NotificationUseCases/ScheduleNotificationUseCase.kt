package com.example.weekly.Domain.Usecase.NotificationUseCases

import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NotificationRepository

/**
 * Use Case для планирования уведомления.
 * 
 * Следует принципу Single Responsibility:
 * - Один UseCase делает одну вещь
 * - Легко тестировать
 * - Легко переиспользовать
 * 
 * Вызывается при сохранении задачи с временем.
 * 
 * @param notificationRepository Репозиторий для уведомлений
 */
class ScheduleNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    
    /**
     * Планирует уведомление для задачи.
     * Если у задачи нет startTime - ничего не происходит.
     * 
     * @param note Задача для которой нужно уведомление
     */
    operator fun invoke(note: Note) {
        if (note.startTime != null) {
            notificationRepository.scheduleNotification(note)
        }
    }
}
