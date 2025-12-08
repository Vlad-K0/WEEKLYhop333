package com.example.weekly.Domain.Usecase.HolidayUseCases

import com.example.weekly.Domain.Model.HolidayInfo
import com.example.weekly.Domain.Repository.HolidayRepository
import java.time.LocalDate

/**
 * Use Case для получения информации о праздниках за неделю.
 * 
 * @param holidayRepository Репозиторий праздников
 */
class GetHolidaysForWeekUseCase(
    private val holidayRepository: HolidayRepository
) {
    
    /**
     * Получить информацию о праздниках для недели.
     * 
     * @param weekStart Начало недели (понедельник)
     * @return Карта: дата -> информация о празднике
     */
    suspend operator fun invoke(weekStart: LocalDate): Map<LocalDate, HolidayInfo> {
        val weekEnd = weekStart.plusDays(6)
        return holidayRepository.getHolidaysForPeriod(weekStart, weekEnd)
    }
}
