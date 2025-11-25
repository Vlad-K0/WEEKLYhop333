package com.example.weekly.Domain.Repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {

    fun getTheme(): Flow<Boolean>

    suspend fun setTheme(isDark: Boolean)
}