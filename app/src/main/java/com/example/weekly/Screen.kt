package com.example.weekly

sealed class Screen(val route: String) {
    // ⭐️ Этот объект обязательно должен присутствовать
    object DayList : Screen("day_list_screen")

    // Этот объект также нужен для перехода на экран деталей
    object DayDetail : Screen("day_detail_screen/{day}") {
        fun createRoute(day: String) = "day_detail_screen/$day"
    }
}