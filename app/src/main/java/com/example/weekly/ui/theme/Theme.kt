package com.example.weekly.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ******************************************************
// ⭐️ ПЕРСОНАЛИЗИРОВАННЫЕ ЦВЕТА ПОЛЬЗОВАТЕЛЯ (Розовый/Индиго)
// ******************************************************

// Основные акценты
private val CustomPrimary = Color(0xFFD283A8) // ⭐️ Приглушенный Розовый (Дела)
private val CustomSecondary = Color(0xFFA6688F) // ⭐️ Глубокий Лиловый (Заметки)

// Цвета для Темной темы
private val DarkBackground = Color(0xFF2B101D) // ⭐️ ИЗМЕНЕНО: Очень глубокий, нейтральный темно-бордовый (ФОН)
private val DarkSurface = Color(0xFF4C1435)      // Темный Бордовый (Поверхность)
private val DarkOnPrimary = Color.Black // Черный текст на светлом розовом
private val DarkOnBackground = Color.White // Белый текст на темном фоне
private val DarkOnSurface = Color.White // Белый текст на бордовой поверхности

// Цвета для Светлой темы
private val LightBackground = Color(0xFFFFFFFF)  // Чисто белый
private val LightSurface = Color(0xFFF4CADB)      // Светлый Розовый (Поверхность)
private val LightOnPrimary = Color.Black
private val LightOnBackground = Color.Black
private val LightOnSurface = Color.Black

// ******************************************************
// ⭐️ ЦВЕТОВЫЕ СХЕМЫ MATERIAL 3
// ******************************************************

private val DarkColorScheme = darkColorScheme(
    primary = CustomPrimary, // Приглушенный Розовый
    onPrimary = DarkOnPrimary,
    primaryContainer = CustomPrimary, // Контейнер Дел (Приглушенный Розовый)
    onPrimaryContainer = DarkOnBackground,
    secondary = CustomSecondary, // Глубокий Лиловый
    onSecondary = DarkOnBackground,
    secondaryContainer = CustomSecondary, // Контейнер Заметок (Глубокий Лиловый)
    onSecondaryContainer = DarkOnBackground,
    tertiary = CustomSecondary,
    background = DarkBackground, // ⭐️ Новый Темно-бордовый Фон
    onBackground = DarkOnBackground,
    surface = DarkSurface, // Темный Бордовый (AppBar, Карточки дней)
    onSurface = DarkOnSurface
)

private val LightColorScheme = lightColorScheme(
    primary = CustomPrimary,
    onPrimary = LightOnPrimary,
    primaryContainer = CustomPrimary,
    onPrimaryContainer = LightOnBackground,
    secondary = CustomSecondary,
    onSecondary = LightOnBackground,
    secondaryContainer = CustomSecondary,
    onSecondaryContainer = LightOnBackground,
    tertiary = CustomSecondary,
    background = LightBackground, // ЧИСТЫЙ БЕЛЫЙ
    onBackground = LightOnBackground,
    surface = LightSurface, // Светлый Розовый (Поверхность)
    onSurface = LightOnSurface
)

@Composable
fun WEEKLYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Отключаем Dynamic Color для принудительного использования кастомных цветов
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Логика Dynamic Color проигнорируется, так как dynamicColor = false
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Используем наши новые схемы
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
