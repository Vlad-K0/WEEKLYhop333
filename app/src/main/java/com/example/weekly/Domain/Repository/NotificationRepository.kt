package com.example.weekly.Domain.Repository

import com.example.weekly.Domain.Model.Note

/**
 * Интерфейс репозитория для управления уведомлениями.
 * 
 * Следует принципам Clean Architecture:
 * - Domain слой определяет интерфейс
 * - Data слой предоставляет реализацию
 * 
 * Это позволяет:
 * - Легко тестировать (можно подменить моком)
 * - Менять реализацию без изменения Domain слоя
 */
interface NotificationRepository {
    
    /**
     * Планирует уведомление для задачи.
     * 
     * @param note Задача с временем startTime
     */
    fun scheduleNotification(note: Note)
    
    /**
     * Отменяет запланированное уведомление.
     * 
     * @param noteId ID задачи
     */
    fun cancelNotification(noteId: Int)
}
