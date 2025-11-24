package com.example.weekly
//закрытый класс (все возможные подклассы определены в одном файле)
sealed class Screen(val route: String) {
    object DayList : Screen("day_list_screen")

    object DayDetail : Screen("day_detail_screen/{day}") {
        //функция для создания фактического URI маршрута, предотвращающая ошибки при ручном форматировании строки.
        fun createRoute(day: String) = "day_detail_screen/$day"
    }
}