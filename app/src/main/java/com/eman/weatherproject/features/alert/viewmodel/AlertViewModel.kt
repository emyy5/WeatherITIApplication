package com.eman.weatherproject.features.alert.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eman.weatherproject.database.model.AlertData
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.repository.RepositoryInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AlertViewModel (private val repo: RepositoryInterface): ViewModel() {

    fun getAllAlertsInVM(): LiveData<List<AlertData>> {
        return repo.getAllAlertsInRepo()
    }

    fun addAlertInVM(alert: AlertData){
        viewModelScope.launch(Dispatchers.IO){
            repo.insertAlertInRepo(alert)
        }
    }

    fun removeAlertInVM(alert: AlertData){
        viewModelScope.launch(Dispatchers.IO){
            repo.deleteAlertInRepo(alert)
        }
    }

    fun setSettingsSharedPrefs(settings: Settings){
        repo.addSettingsToSharedPreferences(settings)
    }

    fun getStoredSettings(): Settings? {
        return repo.getSettingsSharedPreferences()
    }

}
