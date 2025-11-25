package com.example.weekly.Presentation.theme

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



// 🎨 Основные фирменные цвета
private val CustomPrimary = Color(0xFFD283A8)
private val CustomSecondary = Color(0xFFA6688F)

// 🌙 Цвета для ТЕМНОЙ темы
private val DarkBackground = Color(0xFF2B101D)
private val DarkSurface = Color(0xFF4C1435)
private val DarkOnPrimary = Color.Black
private val DarkOnBackground = Color.White
private val DarkOnSurface = Color.White


private val LightBackground = Color(0xFFFFFFFF)
private val LightSurface = Color(0xFFF4CADB)
private val LightOnPrimary = Color.Black
private val LightOnBackground = Color.Black
private val LightOnSurface = Color.Black

private val DarkColorScheme = darkColorScheme(
    primary = CustomPrimary,
    onPrimary = DarkOnPrimary,
    primaryContainer = CustomPrimary,
    onPrimaryContainer = DarkOnBackground,

    secondary = CustomSecondary,
    onSecondary = DarkOnBackground,
    secondaryContainer = CustomSecondary,
    onSecondaryContainer = DarkOnBackground,

    tertiary = CustomSecondary,
    background = DarkBackground,
    onBackground = DarkOnBackground,
    surface = DarkSurface,
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
    background = LightBackground,
    onBackground = LightOnBackground,
    surface = LightSurface,
    onSurface = LightOnSurface
)




@Composable
fun WEEKLYTheme(
    darkTheme: Boolean = isSystemInDarkTheme(), // определяем системную тему
    dynamicColor: Boolean = false, // отключаем Dynamic Color, чтобы использовать кастомную палитру
    content: @Composable () -> Unit
) {
    // определяем, какую схему использовать (тёмную или светлую)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        //кастомные темы
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    //настраиваем цвет статус-бара под тему
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb() // цвет статус-бара = фону приложения
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    //применяем MaterialTheme ко всему UI
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content          // 🌿 переданное содержимое экрана
    )
}
