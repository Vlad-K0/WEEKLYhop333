package com.example.weekly.Data.Repository

import com.example.weekly.Data.Notification.AlarmScheduler
import com.example.weekly.Domain.Model.Note
import com.example.weekly.Domain.Repository.NotificationRepository

/**
 * Реализация репозитория для уведомлений.
 * 
 * Инкапсулирует работу с AlarmScheduler и 
 * реализует интерфейс из Domain слоя.
 * 
 * @param alarmScheduler Планировщик уведомлений
 */
class NotificationRepositoryImpl(
    private val alarmScheduler: AlarmScheduler
) : NotificationRepository {

    override fun scheduleNotification(note: Note) {
        alarmScheduler.scheduleNotification(note)
    }

    override fun cancelNotification(noteId: Int) {
        alarmScheduler.cancelNotification(noteId)
    }
}
