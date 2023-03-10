package com.eman.weatherproject.database.network

import com.eman.weatherproject.database.model.WeatherForecast
import retrofit2.http.GET
import retrofit2.http.Query

interface API_Interface {
    @GET("onecall")
    suspend fun getTheWholeWeather(@Query("lat") lat:Double,
                                   @Query("lon") long:Double,
                                   @Query("units") unit:String,
                                   @Query("exclude") exclude:String,
                                   @Query("lang") lang:String,
                                   @Query("appid")appid:String): WeatherForecast

}