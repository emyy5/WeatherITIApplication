package com.eman.weatherproject

import com.eman.weatherproject.database.model.WeatherForecast


sealed class ApiState{
        class onSuccess(val productData: WeatherForecast):ApiState()
        class onFail(val msg: Throwable ):ApiState()
        object Loading : ApiState()
    }

