package com.example.weekly.Data.Local

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class Converters {

    // Форматтер ISO для LocalTime (например: "14:30:00")
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
        if (timeString == null) return null
        return try {
            LocalTime.parse(timeString, formatter)
        } catch (e: DateTimeParseException) {
            // Старые записи или неправильный формат — безопасно возвращаем null
            null
        }
    }
}