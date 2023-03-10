package com.eman.weatherproject.features.home.viewmodel

import androidx.lifecycle.*
import com.eman.weatherproject.ApiState
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.model.WeatherForecast
import com.eman.weatherproject.database.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel (val repo: Repository): ViewModel() {

    var apiState = MutableStateFlow<ApiState>(ApiState.Loading)
    private lateinit var currentWeather: WeatherForecast
    private val _weatherFromNetwork = MutableLiveData<WeatherForecast>()
    val weatherFromNetwork: LiveData<WeatherForecast> = _weatherFromNetwork

    fun getWholeWeather(lat: Double, long: Double, unit: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = repo.getCurrentWeatherWithLocationInRepo(lat, long, unit)
            _weatherFromNetwork.postValue(response)
        }

    }

//    suspend fun getDataWithFlow(lat: Double, lon: Double, unit: String) =
//        viewModelScope.launch {
//            repo.remoteSource.getCurrentWeatherWithLocationInRepo(lat, lon, unit, "")
//                .catch {
//                    apiState.value = ApiState.onFail(Throwable("Not find Data "))
//                }
//                .collect {
//                    currentWeather = it
//                    apiState.value = ApiState.onSuccess(currentWeather)
//                }
//        }



    fun getStoredSettings(): Settings? {
        return repo.getSettingsSharedPreferences()
    }

    fun getStoredCurrentWeather(): WeatherForecast? {
        return repo.getWeatherSharedPreferences()
    }


    fun addWeatherInVM(weather: WeatherForecast) {
        repo.addWeatherToSharedPreferences(weather)
    }


    fun updateWeatherPrefs(owner: LifecycleOwner) {
        getWholeWeather(
            repo.getWeatherSharedPreferences()?.lat as Double,
            repo.getWeatherSharedPreferences()?.lon as Double,
            "metric"
        )
        weatherFromNetwork.observe(owner) {
            repo.addWeatherToSharedPreferences(it)
        }
    }
}