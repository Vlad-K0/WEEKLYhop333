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
import androidx.compose.ui.Modifier
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


val LOCALE_RU = Locale.forLanguageTag("ru-RU")

@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_DISPLAY = DateTimeFormatter.ofPattern("dd.MM")
@RequiresApi(Build.VERSION_CODES.O)
val DATE_FORMAT_ISO = DateTimeFormatter.ofPattern("yyyy-MM-dd")

// тип элемента, который мы добавляем
enum class NoteType {
    TASK, //со временем
    NOTE //без времени
}



class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val application = application as WeeklyApplication

        val noteViewModelFactory = NoteViewModelFactory(
            repository = application.repository,
            settingsManager = application.settingsManager
        )

        setContent {
            val navController = rememberNavController()

            //инициализируем ViewModel с обновленной Factory
            val noteViewModel: NoteViewModel = viewModel(factory = noteViewModelFactory)

            //собираем текущее состояние темы
            val isDarkTheme by noteViewModel.isDarkTheme.collectAsState()

            //передаем собранное состояние в тему
            WEEKLYTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeeklyNavHost(
                        noteViewModel = noteViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
//навигация между экранами
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
                },
                noteViewModel = noteViewModel
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