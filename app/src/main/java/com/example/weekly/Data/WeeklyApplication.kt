package com.example.weekly.Data

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

import com.example.weekly.di.AppContainer


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")



class WeeklyApplication : Application() {

    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)
    }
}
