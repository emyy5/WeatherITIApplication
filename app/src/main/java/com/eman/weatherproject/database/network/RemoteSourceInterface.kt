package com.eman.weatherproject.database.network

import com.eman.weatherproject.database.model.WeatherForecast

interface RemoteSourceInterface {
    suspend fun getCurrentWeatherWithLocation(
        lat: Double,
        long: Double, unit: String,
        lang: String
    ): WeatherForecast

}