package com.eman.weatherproject.database.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.eman.weatherproject.database.model.WeatherAddress

@Dao
interface FavouriteDao {
    @Query("SELECT * FROM addresses")
    fun myAllAddress(): LiveData<List<WeatherAddress>>

    @Insert
    fun insertFavAddress(address: WeatherAddress)

    @Delete
    fun deleteFavAddress(address: WeatherAddress)
}

