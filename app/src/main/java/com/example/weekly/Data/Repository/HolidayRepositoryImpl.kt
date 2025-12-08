package com.example.weekly.Data.Repository

import com.example.weekly.Data.Local.RussianHolidays
import com.example.weekly.Data.Remote.IsDayOffApiService
import com.example.weekly.Domain.Model.DayType
import com.example.weekly.Domain.Model.HolidayInfo
import com.example.weekly.Domain.Repository.HolidayRepository
import java.time.DayOfWeek
import java.time.LocalDate

/**
 * Реализация репозитория для праздников.
 * 
 * Объединяет данные из API isdayoff.ru и статического справочника
 * русских праздников для получения полной информации о днях.
 */
class HolidayRepositoryImpl(
    private val apiService: IsDayOffApiService
) : HolidayRepository {

    override suspend fun getHolidaysForPeriod(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, HolidayInfo> {
        
        val result = mutableMapOf<LocalDate, HolidayInfo>()
        
        // Получаем данные из API
        val apiResponse = apiService.getDayTypes(startDate, endDate)
        
        // Генерируем список дат
        val dates = generateSequence(startDate) { it.plusDays(1) }
            .takeWhile { !it.isAfter(endDate) }
            .toList()
        
        dates.forEachIndexed { index, date ->
            // Определяем тип дня
            val apiDayType = apiResponse?.getOrNull(index)
            val dayType = when (apiDayType) {
                '0' -> DayType.WORKING
                '1' -> DayType.HOLIDAY
                '2' -> DayType.PRE_HOLIDAY
                else -> {
                    // Fallback: если API недоступен, проверяем выходные
                    when (date.dayOfWeek) {
                        DayOfWeek.SATURDAY, DayOfWeek.SUNDAY -> DayType.WEEKEND
                        else -> DayType.WORKING
                    }
                }
            }
            
            // Получаем название праздника из справочника
            val holidayName = RussianHolidays.getHolidayName(date)
            
            // Добавляем только праздничные/выходные дни
            if (dayType != DayType.WORKING || holidayName != null) {
                result[date] = HolidayInfo(
                    date = date,
                    dayType = if (holidayName != null) DayType.HOLIDAY else dayType,
                    holidayName = holidayName
                )
            }
        }
        
        return result
    }
}
