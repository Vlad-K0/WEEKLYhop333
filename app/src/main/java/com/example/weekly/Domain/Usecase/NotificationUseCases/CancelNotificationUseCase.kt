package com.example.weekly.Domain.Usecase.NotificationUseCases

import com.example.weekly.Domain.Repository.NotificationRepository

/**
 * Use Case для отмены уведомления.
 * 
 * Вызывается при:
 * - Удалении задачи
 * - Изменении времени задачи
 * - Отметке задачи как выполненной
 * 
 * @param notificationRepository Репозиторий для уведомлений
 */
class CancelNotificationUseCase(
    private val notificationRepository: NotificationRepository
) {
    
    /**
     * Отменяет запланированное уведомление.
     * 
     * @param noteId ID задачи
     */
    operator fun invoke(noteId: Int) {
        notificationRepository.cancelNotification(noteId)
    }
}
