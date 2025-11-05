package com.example.weekly

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.weekly.data.NoteViewModel
import com.example.weekly.data.NoteViewModelFactory
import com.example.weekly.data.WeeklyApplication
import com.example.weekly.ui.theme.WEEKLYTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// ******************************************************
// КОНСТАНТЫ И ENUM (Оставлены здесь, так как они используются почти везде)
// ******************************************************

val LOCALE_RU = Locale.forLanguageTag("ru-RU")

@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_DISPLAY = DateTimeFormatter.ofPattern("dd.MM")
@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// Тип элемента, который мы добавляем
enum class NoteType {
    TASK, // Дело (со временем)
    NOTE // Заметка (без времени)
}

// ******************************************************
// ГЛАВНАЯ ACTIVITY И NAV HOST
// ******************************************************

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем Application и Repository
        val application = application as WeeklyApplication

        setContent {
            WEEKLYTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    WeeklyNavHost(
                        noteViewModel = viewModel(
                            factory = NoteViewModelFactory(application.repository)
                        ),
                        navController = navController
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyNavHost(noteViewModel: NoteViewModel, navController: NavHostController) {

    val groupedNotes by noteViewModel.notesGroupedByDay.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Screen.DayList.route
    ) {
        composable(Screen.DayList.route) {
            DayListScreen(
                groupedNotes = groupedNotes,
                onDayClick = { dayISO ->
                    navController.navigate(Screen.DayDetail.createRoute(dayISO))
                }
            )
        }

        composable(
            route = Screen.DayDetail.route,
            arguments = listOf(navArgument("day") { type = NavType.StringType })
        ) { backStackEntry ->
            val selectedDay = backStackEntry.arguments?.getString("day") ?: LocalDate.now().format(DATE_FORMAT_ISO)

            DayDetailScreen(
                selectedDay = selectedDay,
                noteViewModel = noteViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}