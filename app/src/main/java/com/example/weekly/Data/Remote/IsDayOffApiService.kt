package com.example.weekly.Data.Remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * HTTP клиент для API isdayoff.ru
 * 
 * API возвращает строку цифр, где каждая цифра соответствует дню:
 * - 0: рабочий день
 * - 1: нерабочий день (праздник/выходной)
 * - 2: сокращённый рабочий день
 * 
 * Пример: для периода 01.01 - 07.01 вернёт "1111111"
 */
class IsDayOffApiService {

    companion object {
        private const val BASE_URL = "https://isdayoff.ru/api/getdata"
        private val DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd")
        private const val TIMEOUT_MS = 10000
    }

    /**
     * Получить типы дней для диапазона дат.
     * 
     * @param startDate Начальная дата
     * @param endDate Конечная дата
     * @return Строка цифр (0, 1, 2) для каждого дня или null при ошибке
     */
    suspend fun getDayTypes(startDate: LocalDate, endDate: LocalDate): String? {
        return withContext(Dispatchers.IO) {
            try {
                val startFormatted = startDate.format(DATE_FORMAT)
                val endFormatted = endDate.format(DATE_FORMAT)
                
                val urlString = "$BASE_URL?date1=$startFormatted&date2=$endFormatted&pre=1"
                val url = URL(urlString)
                
                val connection = url.openConnection() as HttpURLConnection
                connection.apply {
                    requestMethod = "GET"
                    connectTimeout = TIMEOUT_MS
                    readTimeout = TIMEOUT_MS
                }
                
                if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } else {
                    null
                }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }
}
