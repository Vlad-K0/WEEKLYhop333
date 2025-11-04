package com.example.weekly

sealed class Screen(val route: String) {
    // Главный экран: просто список дней
    object Planner : Screen("planner_screen")

    // Экран деталей дня: принимает аргумент 'day'
    // Аргумент передаётся в URL-стиле: day_detail_screen/{day}
    object DayDetail : Screen("day_detail_screen/{day}") {
        // Функция для создания полного маршрута с аргументом
        fun createRoute(day: String) = "day_detail_screen/$day"
    }
}