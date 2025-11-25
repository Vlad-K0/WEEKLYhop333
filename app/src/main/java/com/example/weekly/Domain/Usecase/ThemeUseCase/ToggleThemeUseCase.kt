package com.example.weekly.Domain.Usecase.ThemeUseCase

import com.example.weekly.Domain.Repository.SettingsRepository

class ToggleThemeUseCase(private val settingsRepository: SettingsRepository) {
    suspend operator fun invoke(isDark: Boolean){
        settingsRepository.setTheme(isDark)
    }

}