package com.example.weekly.Data.Local

import java.time.LocalDate
import java.time.MonthDay

/**
 * Статический справочник русских государственных праздников.
 * 
 * Используется для определения названия праздника по дате,
 * так как API isdayoff.ru не предоставляет названия.
 */
object RussianHolidays {

    /**
     * Карта праздников: MonthDay -> Название праздника
     */
    private val holidays: Map<MonthDay, String> = mapOf(
        // Новогодние каникулы
        MonthDay.of(1, 1) to "Новый год",
        MonthDay.of(1, 2) to "Новогодние каникулы",
        MonthDay.of(1, 3) to "Новогодние каникулы",
        MonthDay.of(1, 4) to "Новогодние каникулы",
        MonthDay.of(1, 5) to "Новогодние каникулы",
        MonthDay.of(1, 6) to "Новогодние каникулы",
        MonthDay.of(1, 7) to "Рождество Христово",
        MonthDay.of(1, 8) to "Новогодние каникулы",
        
        // Февраль
        MonthDay.of(2, 23) to "День защитника Отечества",
        
        // Март
        MonthDay.of(3, 8) to "Международный женский день",
        
        // Май
        MonthDay.of(5, 1) to "Праздник Весны и Труда",
        MonthDay.of(5, 9) to "День Победы",
        
        // Июнь
        MonthDay.of(6, 12) to "День России",
        
        // Ноябрь
        MonthDay.of(11, 4) to "День народного единства"
    )

    /**
     * Получить название праздника по дате.
     * 
     * @param date Дата для проверки
     * @return Название праздника или null если это не праздник
     */
    fun getHolidayName(date: LocalDate): String? {
        val monthDay = MonthDay.of(date.monthValue, date.dayOfMonth)
        return holidays[monthDay]
    }

    /**
     * Проверить, является ли дата праздником.
     */
    fun isHoliday(date: LocalDate): Boolean {
        return getHolidayName(date) != null
    }
}
