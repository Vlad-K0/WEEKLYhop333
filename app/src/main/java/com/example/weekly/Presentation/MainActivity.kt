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
import com.example.weekly.Data.WeeklyApplication
import com.example.weekly.Presentation.Navigation.WeeklyNavGraph
import com.example.weekly.Presentation.ViewModel.NoteViewModel
import com.example.weekly.Presentation.theme.WEEKLYTheme

/**
 * Главная (единственная) Activity приложения WEEKLY.
 * 
 * Служит контейнером для Compose UI и выполняет следующие функции:
 * - Инициализация навигации
 * - Создание ViewModel через Factory
 * - Применение темы приложения
 * - Отображение навигационного графа
 */
class MainActivity : ComponentActivity() {
    
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Получаем DI контейнер из Application
        val application = application as WeeklyApplication
        val noteViewModelFactory = application.container.noteViewModelFactory

        setContent {
            // Инициализация контроллера навигации
            val navController = rememberNavController()

            // Инициализация ViewModel через Factory с зависимостями
            val noteViewModel: NoteViewModel = viewModel(factory = noteViewModelFactory)

            // Подписка на состояние UI для динамического применения темы
            val state by noteViewModel.uiState.collectAsState()

            // Применение темы и отображение UI
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