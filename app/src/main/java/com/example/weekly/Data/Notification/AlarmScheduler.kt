package com.example.weekly.Data.Notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.example.weekly.Domain.Model.Note
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

/**
 * Класс для планирования уведомлений с помощью AlarmManager.
 * 
 * AlarmManager используется вместо WorkManager, потому что:
 * - Нужно точное время срабатывания (exact alarm)
 * - WorkManager оптимизирован для фоновых задач, а не для точного времени
 * 
 * @param context Контекст приложения
 */
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    companion object {
        const val EXTRA_NOTE_ID = "extra_note_id"
        const val EXTRA_NOTE_CONTENT = "extra_note_content"
    }

    /**
     * Планирует уведомление для заметки.
     * 
     * @param note Заметка с временем startTime
     */
    fun scheduleNotification(note: Note) {
        // Если нет времени - не планируем
        val startTime = note.startTime ?: return

        // Вычисляем время срабатывания
        val triggerTime = calculateTriggerTime(note.date, startTime)
        
        // Если время уже прошло - не планируем
        if (triggerTime <= System.currentTimeMillis()) return

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(EXTRA_NOTE_ID, note.id)
            putExtra(EXTRA_NOTE_CONTENT, note.content)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            note.id, // Используем ID заметки как requestCode
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Планируем точный будильник (ВАЖНО для уведомлений по времени)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    /**
     * Отменяет запланированное уведомление.
     * 
     * @param noteId ID заметки для отмены
     */
    fun cancelNotification(noteId: Int) {
        val intent = Intent(context, NotificationReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            noteId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    /**
     * Вычисляет время срабатывания в миллисекундах.
     * 
     * @param dateString Дата в формате "yyyy-MM-dd"
     * @param time Время срабатывания
     * @return Время в миллисекундах (epoch)
     */
    private fun calculateTriggerTime(dateString: String, time: LocalTime): Long {
        val date = LocalDate.parse(dateString)
        val dateTime = LocalDateTime.of(date, time)
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    }
}
