package com.eman.weatherproject.features.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eman.weatherproject.database.repository.RepositoryInterface

class SettingViewModelFactory(private val repo: RepositoryInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SettingViewModel(repo) as T
    }
}