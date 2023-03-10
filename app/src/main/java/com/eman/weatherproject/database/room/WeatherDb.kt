package com.eman.weatherproject.database.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.eman.weatherproject.database.model.AlertData
import com.eman.weatherproject.database.model.WeatherAddress
import com.eman.weatherproject.database.model.WeatherForecast


@Database(entities = arrayOf(WeatherForecast::class, WeatherAddress::class, AlertData::class), version = 1)
@TypeConverters(WeatherConverter::class)
abstract class WeatherDb : RoomDatabase(){

    abstract fun weatherDao(): WeatherDAO
    abstract fun addressesDao(): FavouriteDao
    abstract fun alertsDao(): AlertsDAO

    companion object{
        private var weatherDatabase: WeatherDb? = null

        fun getInstance(context: Context): WeatherDb {
            return weatherDatabase ?: synchronized(this){
                val instance = Room.databaseBuilder(context.applicationContext, WeatherDb::class.java, "WeatherDatabase").build()
                weatherDatabase = instance
                instance
            }
        }
    }

}