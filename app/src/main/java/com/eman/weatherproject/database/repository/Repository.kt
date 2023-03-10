package com.eman.weatherproject.database.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import com.eman.weatherproject.*
import com.eman.weatherproject.database.model.AlertData
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.model.WeatherAddress
import com.eman.weatherproject.database.model.WeatherForecast
import com.eman.weatherproject.database.network.RemoteSourceInterface
import com.eman.weatherproject.database.room.LocalSourceInterface
import com.eman.weatherproject.utilities.CURRENT_WEATHER
import com.eman.weatherproject.utilities.MyLanguage
import com.eman.weatherproject.utilities.SAVING_SETTINGS_IN_SHARED_PREFERENCES
import com.google.gson.Gson

class Repository(var remoteSource: RemoteSourceInterface, var localSource: LocalSourceInterface,
                 var context: Context, var mySharedPre: SharedPreferences
) : RepositoryInterface {

    private lateinit var settings: Settings
    lateinit var WeatherList: WeatherForecast

    companion object {
        private var instance: Repository? = null
        fun getInstance(
            remoteSource: RemoteSourceInterface,
            localSource: LocalSourceInterface,
            context: Context,
            addSharedpre: SharedPreferences
        ): Repository {
            return instance ?: Repository(remoteSource, localSource, context, addSharedpre)
        }
    }

    override suspend fun getCurrentWeatherWithLocationInRepo(lat: Double, long: Double, unit: String, ): WeatherForecast {
        settings = Settings()
        if(getSettingsSharedPreferences()?.language as Boolean) {
            var weatherinrepo =
                remoteSource.getCurrentWeatherWithLocation(lat, long, unit, MyLanguage.en.convertLanguage)
            return weatherinrepo
        }
            var weatherinrepo = remoteSource.getCurrentWeatherWithLocation(lat, long, unit, MyLanguage.ar.convertLanguage)
            return weatherinrepo

    }

    override val storedAddresses: LiveData<List<WeatherAddress>>
        get() = localSource.getAllAddresses()


    override fun getAllWeathersInRepo(): LiveData<List<WeatherForecast>> {
        return localSource.getAllStoredWeathers()
    }

    override fun getOneWeather(lat: Double, long: Double): LiveData<WeatherForecast> {
        return localSource.getWeatherWithLatLong(lat,long)
    }

    override fun insertFavoriteAddress(address: WeatherAddress) {
        localSource.insertFavoriteAddress(address)
    }

    override fun deleteFavoriteAddress(address: WeatherAddress) {
        localSource.deleteFavoriteAddress(address)
    }

    override fun insertWeather(weather: WeatherForecast) {
        localSource.insertWeather(weather)
    }

    override fun deleteWeather(weather: WeatherForecast) {
        localSource.deleteWeather(weather)
    }


    override fun addSettingsToSharedPreferences(settings: Settings) {
        var prefEditor = mySharedPre.edit()
        var gson = Gson()
        var settingStr = gson.toJson(settings)
        prefEditor.putString(SAVING_SETTINGS_IN_SHARED_PREFERENCES, settingStr)
        prefEditor.commit()
    }

    override fun getSettingsSharedPreferences(): Settings? {
        var settingStr = mySharedPre.getString(SAVING_SETTINGS_IN_SHARED_PREFERENCES, "")
        var gson = Gson()
        var settingsObj = gson.fromJson(settingStr, Settings::class.java)
        return settingsObj
    }

    override fun addWeatherToSharedPreferences(weather: WeatherForecast) {
        var prefEditor = mySharedPre.edit()
        var gson = Gson()
        var weatherStr = gson.toJson(weather)
        prefEditor.putString(CURRENT_WEATHER, weatherStr)
        prefEditor.commit()
    }

    override fun getWeatherSharedPreferences(): WeatherForecast? {
        var weatherStr = mySharedPre.getString(CURRENT_WEATHER, "")
        var gson = Gson()
        var weatherObj: WeatherForecast? = gson.fromJson(weatherStr, WeatherForecast::class.java)
        return weatherObj
    }

    override fun getAllAlertsInRepo(): LiveData<List<AlertData>> {
        return localSource.getAllStoredAlerts()
    }

    override fun insertAlertInRepo(alert: AlertData) {
        localSource.insertAlert(alert)
    }

    override fun deleteAlertInRepo(alert: AlertData) {
        localSource.deleteAlert(alert)
    }

}

