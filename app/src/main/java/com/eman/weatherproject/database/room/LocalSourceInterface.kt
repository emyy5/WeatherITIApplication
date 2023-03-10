package com.eman.weatherproject.database.room

import androidx.lifecycle.LiveData
import com.eman.weatherproject.database.model.AlertData
import com.eman.weatherproject.database.model.WeatherAddress
import com.eman.weatherproject.database.model.WeatherForecast

interface LocalSourceInterface {

    //weather addresses funs
    fun getAllAddresses(): LiveData<List<WeatherAddress>>

    fun insertFavoriteAddress(address: WeatherAddress)

    fun deleteFavoriteAddress(address: WeatherAddress)

    //weather funs
    fun getAllStoredWeathers(): LiveData<List<WeatherForecast>>

    fun getWeatherWithLatLong(lat:Double,long:Double): LiveData<WeatherForecast>

    fun insertWeather(weather: WeatherForecast)

    fun deleteWeather(weather: WeatherForecast)

    //alerts funs
    fun getAllStoredAlerts(): LiveData<List<AlertData>>

    fun insertAlert(alert: AlertData)

    fun deleteAlert(alert: AlertData)
}
