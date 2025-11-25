package com.example.weekly.Domain.Usecase.ThemeUseCase

import com.example.weekly.Domain.Repository.SettingsRepository
import kotlinx.coroutines.flow.Flow

class GetThemeUseCase(private val settingsRepository: SettingsRepository) {
    operator fun invoke(): Flow<Boolean> {
        return settingsRepository.getTheme()
    }
}