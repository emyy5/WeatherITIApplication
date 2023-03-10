package com.eman.weatherproject.features.alert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.eman.weatherproject.database.repository.RepositoryInterface

class AlertViewModelFactory(private val repo: RepositoryInterface): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AlertViewModel(repo) as T
    }
}