package com.example.weekly.Presentation

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.weekly.Presentation.ViewModel.NoteViewModel
import com.example.weekly.Data.WeeklyApplication
import com.example.weekly.Presentation.theme.WEEKLYTheme
import com.example.weekly.Presentation.Navigation.WeeklyNavGraph
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
        val noteViewModelFactory = application.container.noteViewModelFactory

        setContent {
            val navController = rememberNavController()

            //инициализируем ViewModel с обновленной Factory
            val noteViewModel: NoteViewModel = viewModel(factory = noteViewModelFactory)

            //собираем текущее состояние из uiState
            val state by noteViewModel.uiState.collectAsState()

            //передаем собранное состояние в тему
            WEEKLYTheme(darkTheme = state.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WeeklyNavGraph(
                        noteViewModel = noteViewModel,
                        navController = navController
                    )
                }
            }
        }
    }
}