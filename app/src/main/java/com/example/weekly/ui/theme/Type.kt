package com.example.weekly.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.weekly.R

// ******************************************************
// СЕМЕЙСТВА ПОЛЬЗОВАТЕЛЬСКИХ ШРИФТОВ
// ******************************************************

// 1. Рукописный шрифт (Marck Script) для заголовков и акцентов
val AppHandwritingFamily = FontFamily(
    // ⭐️ Используйте R.font.marck_script_regular (файл, который у вас был ранее)
    Font(R.font.marck_script_regular, FontWeight.Normal),
    Font(R.font.marck_script_regular, FontWeight.Bold),
)

// 2. Andika для основного текста (заметки, даты, кнопки)
val AppBodyFamily = FontFamily(
    Font(R.font.andika_regular, FontWeight.Normal),  // ⭐️ andika_regular.ttf
    Font(R.font.andika_italic, FontWeight.Normal, FontStyle.Italic), // ⭐️ andika_italic.ttf
    Font(R.font.andika_bold, FontWeight.Bold),       // ⭐️ andika_bold.ttf
)


val Typography = Typography(
    // ----------------------------------------------------
    // titleLarge: App Title и Week Range (РУКОПИСНЫЙ)
    // ----------------------------------------------------

    titleLarge = TextStyle(
        fontFamily = AppHandwritingFamily, // Marck Script
        fontWeight = FontWeight.Bold,
        fontStyle = FontStyle.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 1.sp
    ),
    // ----------------------------------------------------
    // titleMedium: Названия дней (РУКОПИСНЫЙ, для стиля)
    // ----------------------------------------------------

    titleMedium = TextStyle(
        fontFamily = AppHandwritingFamily, // Marck Script
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.5.sp
    ),

    // ----------------------------------------------------
    // bodyMedium: Контент заметок/дел, сниппеты, даты (ANDIKA)
    // ----------------------------------------------------

    bodyMedium = TextStyle(
        fontFamily = AppBodyFamily, // ⭐️ Andika
        fontWeight = FontWeight.Normal,
        fontStyle = FontStyle.Normal,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        letterSpacing = 0.sp
    ),
    // ----------------------------------------------------

    // labelLarge: Кнопки (ANDIKA)
    labelLarge = TextStyle(
        fontFamily = AppBodyFamily, // ⭐️ Andika
        fontWeight = FontWeight.Medium, // Используем Medium (будет Bold, т.к. нет Medium) для акцента
        fontStyle = FontStyle.Normal,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)