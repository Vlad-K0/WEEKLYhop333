package com.example.weekly.Presentation

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Константы для форматирования дат и локализации,
 * используемые в Presentation слое приложения WEEKLY.
 */

// Локаль для русского языка
val LOCALE_RU: Locale = Locale.forLanguageTag("ru-RU")

// Формат даты для отображения (день.месяц, например "25.11")
@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_DISPLAY: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM")

// Формат даты ISO (год-месяц-день, например "2025-11-25")
@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_ISO: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
