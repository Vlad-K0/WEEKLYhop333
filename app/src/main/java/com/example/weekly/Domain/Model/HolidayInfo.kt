package com.example.weekly.Domain.Model

import java.time.LocalDate

/**
 * Тип дня в календаре.
 */
enum class DayType {
    WORKING,      // 0 - Рабочий день
    HOLIDAY,      // 1 - Праздник/выходной
    PRE_HOLIDAY,  // 2 - Сокращённый рабочий день
    WEEKEND       // Обычные выходные (сб, вс)
}

/**
 * Информация о праздничном/выходном дне.
 * 
 * @param date Дата
 * @param dayType Тип дня
 * @param holidayName Название праздника (если есть)
 */
data class HolidayInfo(
    val date: LocalDate,
    val dayType: DayType,
    val holidayName: String? = null
)
