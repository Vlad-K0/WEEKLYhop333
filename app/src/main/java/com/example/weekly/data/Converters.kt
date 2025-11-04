package com.example.weekly.data

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Преобразователь типов для сохранения LocalTime в базу данных Room.
 * LocalTime сохраняется в виде строки формата "HH:mm:ss.zzz".
 */
class Converters {

    @RequiresApi(Build.VERSION_CODES.O)
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? {
        return time?.format(formatter)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun toLocalTime(timeString: String?): LocalTime? {
        if (timeString == null) {
            return null
        }
        return try {
            LocalTime.parse(timeString, formatter)
        } catch (e: DateTimeParseException) {
            // Если парсинг не удался (старые записи или ошибка), возвращаем null
            null
        }
    }
}