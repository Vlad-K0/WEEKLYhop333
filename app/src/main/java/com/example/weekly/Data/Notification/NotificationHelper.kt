package com.example.weekly.Data.Notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.weekly.R

/**
 * Helper класс для создания и отображения уведомлений.
 * 
 * Инкапсулирует логику работы с системой уведомлений Android:
 * - Создание NotificationChannel (для Android 8+)
 * - Построение и показ уведомлений
 * 
 * @param context Контекст приложения для доступа к NotificationManager
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "weekly_task_notifications"
        const val CHANNEL_NAME = "Напоминания о задачах"
        const val CHANNEL_DESCRIPTION = "Уведомления о задачах в запланированное время"
    }

    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannel()
    }

    /**
     * Создаёт канал уведомлений (обязательно для Android 8.0+).
     * Канал создаётся один раз при инициализации.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = CHANNEL_DESCRIPTION
                enableVibration(true)
                enableLights(true)
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    /**
     * Показывает уведомление с заданным заголовком и текстом.
     * 
     * @param noteId ID заметки (используется как ID уведомления)
     * @param title Заголовок уведомления (текст задачи)
     * @param message Сообщение уведомления
     */
    fun showNotification(noteId: Int, title: String, message: String = "Время выполнить задачу!") {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(0, 500, 200, 500))
            .build()

        notificationManager.notify(noteId, notification)
    }

    /**
     * Отменяет уведомление по ID.
     * 
     * @param noteId ID заметки/уведомления для отмены
     */
    fun cancelNotification(noteId: Int) {
        notificationManager.cancel(noteId)
    }
}
