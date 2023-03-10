package com.eman.weatherproject.database.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    const val baseURL = "https://api.openweathermap.org/data/2.5/"
    val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create())
        .baseUrl(baseURL)
        .build()

    object API {
        val retrofitService : API_Interface by lazy { retrofit.create(API_Interface::class.java) }
    }
}



