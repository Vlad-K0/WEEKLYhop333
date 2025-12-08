package com.example.weekly.Data.Notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * BroadcastReceiver для обработки сигналов от AlarmManager.
 * 
 * Когда AlarmManager срабатывает в запланированное время,
 * этот Receiver получает Intent и показывает уведомление.
 * 
 * Зарегистрирован в AndroidManifest.xml:
 * <receiver android:name=".Data.Notification.NotificationReceiver" android:exported="false"/>
 */
class NotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val noteId = intent.getIntExtra(AlarmScheduler.EXTRA_NOTE_ID, -1)
        val noteContent = intent.getStringExtra(AlarmScheduler.EXTRA_NOTE_CONTENT) ?: "Задача"

        if (noteId != -1) {
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showNotification(
                noteId = noteId,
                title = noteContent,
                message = "Время выполнить задачу!"
            )
        }
    }
}
