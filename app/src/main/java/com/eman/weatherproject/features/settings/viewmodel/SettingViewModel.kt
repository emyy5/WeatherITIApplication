package com.eman.weatherproject.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.repository.RepositoryInterface

class SettingViewModel (private val repo: RepositoryInterface): ViewModel(){
    fun setSettingsSharedPrefs(settings: Settings){
        repo.addSettingsToSharedPreferences(settings)
    }
    fun getStoredSettings(): Settings?{
        return repo.getSettingsSharedPreferences()
    }
}