package com.eman.weatherproject.database.room

import com.eman.weatherproject.database.model.WeatherAddress
import com.eman.weatherproject.database.model.WeatherForecast

interface FavClickInterface {
    fun onRemoveBtnClick(address: WeatherAddress, weather: WeatherForecast)
    fun onFavItemClick(address: WeatherAddress)
}