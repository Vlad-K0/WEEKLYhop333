package com.example.weekly.Data.Repository

import com.example.weekly.Data.Settings.SettingsManager
import com.example.weekly.Domain.Repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(private val settingsManager: SettingsManager): SettingsRepository {
    override fun getTheme(): Flow<Boolean> {
        return settingsManager.isDarkTheme
    }

    override suspend fun setTheme(isDark: Boolean) {
        settingsManager.toggleTheme(isDark)
    }

}