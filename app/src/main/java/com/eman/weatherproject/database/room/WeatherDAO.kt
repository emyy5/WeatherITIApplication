package com.eman.weatherproject.database.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.eman.weatherproject.database.model.WeatherForecast

@Dao
interface WeatherDAO {

    @Query("SELECT * FROM weathers")
    fun myAllWeather(): LiveData<List<WeatherForecast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeather(weather: WeatherForecast)

    @Delete
    fun deleteWeather(weather: WeatherForecast)

    @Query("SELECT * FROM weathers WHERE lat == :latt AND lon == :longg")
    fun weatherWithLatAndLong(latt:Double, longg:Double): LiveData<WeatherForecast>
}

