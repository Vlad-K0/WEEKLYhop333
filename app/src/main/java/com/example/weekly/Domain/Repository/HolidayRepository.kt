package com.example.weekly.Domain.Repository

import com.example.weekly.Domain.Model.HolidayInfo
import java.time.LocalDate

/**
 * Интерфейс репозитория для получения информации о праздниках.
 */
interface HolidayRepository {
    
    /**
     * Получить информацию о праздниках для диапазона дат.
     * 
     * @param startDate Начальная дата
     * @param endDate Конечная дата
     * @return Карта: дата -> информация о празднике
     */
    suspend fun getHolidaysForPeriod(
        startDate: LocalDate,
        endDate: LocalDate
    ): Map<LocalDate, HolidayInfo>
}
