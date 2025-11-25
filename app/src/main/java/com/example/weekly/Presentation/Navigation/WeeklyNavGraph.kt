package com.example.weekly.Presentation.Navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.weekly.Presentation.DATE_FORMAT_ISO
import com.example.weekly.Presentation.Screen.DayDetailScreen
import com.example.weekly.Presentation.Screen.DayListScreen
import com.example.weekly.Presentation.Screen.Screen
import com.example.weekly.Presentation.ViewModel.NoteViewModel
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyNavGraph(
    noteViewModel: NoteViewModel,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = Screen.DayList.route
    ) {
        composable(Screen.DayList.route) {
            DayListScreen(
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
            val selectedDay = backStackEntry.arguments?.getString("day") 
                ?: LocalDate.now().format(DATE_FORMAT_ISO)

            DayDetailScreen(
                selectedDay = selectedDay,
                noteViewModel = noteViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
