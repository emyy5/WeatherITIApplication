package com.eman.weatherproject.database.room

import androidx.room.TypeConverter
import com.eman.weatherproject.database.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class WeatherConverter {

    var gson = Gson()
    //current weather
    @TypeConverter
    fun currentWeatherToString(currentWeather: CurrentWeather):String{
        return gson.toJson(currentWeather)
    }
    @TypeConverter
    fun stringToCurrentWeather(currentWeatherString: String): CurrentWeather {
        return gson.fromJson(currentWeatherString, CurrentWeather::class.java)
    }

    //weather
    @TypeConverter
    fun weatherToString(weather: Weather):String{
        return gson.toJson(weather)
    }
    @TypeConverter
    fun stringToWeather(weatherString:String): Weather {
        return gson.fromJson(weatherString, Weather::class.java)
    }

    //weather list
    @TypeConverter
    fun weatherListToString(weatherList: List<Weather>):String{
        return gson.toJson(weatherList)
    }
    @TypeConverter
    fun stringToWeatherList(weatherListString:String):List<Weather>{
        var list = object : TypeToken<List<Weather>>(){}.type
        return gson.fromJson(weatherListString, list)
    }

    //hourly
    @TypeConverter
    fun hourlyWeatherToString(hourlyWeather: HourlyWeather):String{
        return gson.toJson(hourlyWeather)
    }
    @TypeConverter
    fun stringToHourlyWeather(hourlyWeatherString:String): HourlyWeather {
        return gson.fromJson(hourlyWeatherString, HourlyWeather::class.java)
    }

    //hourly list
    @TypeConverter
    fun hourlyListToString(hourlyList:List<HourlyWeather>):String{
        return gson.toJson(hourlyList)
    }
    @TypeConverter
    fun stringToHourlyList(hourlyListString:String):List<HourlyWeather>{
        var list = object : TypeToken<List<HourlyWeather>>(){}.type
        return gson.fromJson(hourlyListString,list)
    }

    //daily
    @TypeConverter
    fun dailyWeatherToString(dailyWeather: DailyWeather):String{
        return gson.toJson(dailyWeather)
    }
    @TypeConverter
    fun stringToDailyWeather(dailyWeatherString:String): DailyWeather {
        return gson.fromJson(dailyWeatherString, DailyWeather::class.java)
    }

    //daily list
    @TypeConverter
    fun dailyListToString(dailyList:List<DailyWeather>):String{
        return gson.toJson(dailyList)
    }
    @TypeConverter
    fun stringToDailyList(dailyListString:String):List<DailyWeather>{
        var list = object : TypeToken<List<DailyWeather>>(){}.type
        return gson.fromJson(dailyListString,list)
    }

    //temprature
    @TypeConverter
    fun tempratureToString(temprature: Temprature):String{
        return gson.toJson(temprature)
    }
    @TypeConverter
    fun stringToTemprature(tempratureString:String): Temprature {
        return gson.fromJson(tempratureString, Temprature::class.java)
    }

    //alert list
    /*@TypeConverter
    fun alertListToString(allAlerts:List<Alert>):String{
        return gson.toJson(allAlerts)
    }

    @TypeConverter
    fun stringToAlertsList(allAlertString:String):List<Alert>{
        var list = object :TypeToken<List<Alert>>(){}.type
        return gson.fromJson(allAlertString,list)
    }

    //alert
    @TypeConverter
    fun alertToString(oneAlert:Alert):String{
        return gson.toJson(oneAlert)
    }

    @TypeConverter
    fun stringToAlert(oneAlertString:String):Alert{
        var list = object :TypeToken<Alert>(){}.type
        return gson.fromJson(oneAlertString,list)
    }*/

    //tag
    @TypeConverter
    fun tagToString(oneTag:List<String>):String{
        return gson.toJson(oneTag)
    }

    @TypeConverter
    fun stringToTags(oneTagString:String):List<String>{
        var list = object : TypeToken<List<String>>(){}.type
        return gson.fromJson(oneTagString,list)
    }

    //covert from / to alerts
    @TypeConverter
    fun fromAlertToString(alertList: List<Alert>?) : String{
        return gson.toJson(alertList)
    }

    @TypeConverter
    fun fromStringToAlert(alertListString: String) : List<Alert>?{
        if(alertListString == null){
            return Collections.emptyList()
        }else{
            var list = object : TypeToken<List<Alert?>?>(){}.type
            return gson.fromJson(alertListString, list)
        }
    }

    @TypeConverter
    fun fromAlertDataToString(alertList: List<AlertData>) : String{
        return gson.toJson(alertList)
    }

    @TypeConverter
    fun fromStringToAlertData(alertListString: String) : List<AlertData> {
        var list = object : TypeToken<List<AlertData>>(){}.type
        return gson.fromJson(alertListString, list)
    }

    @TypeConverter
    fun fromOneAlertDataToString(alert: AlertData) : String{
        return gson.toJson(alert)
    }

    @TypeConverter
    fun fromStringToOneAlertData(alertString: String) : AlertData {
        var list = object : TypeToken<AlertData>(){}.type
        return gson.fromJson(alertString, list)
    }

    @TypeConverter
    fun fromDateToString(date: Date):String{
        return gson.toJson(date)
    }

    @TypeConverter
    fun fromStringToDate(dateString:String): Date {
        var date = object : TypeToken<Date>(){}.type
        return gson.fromJson(dateString,date)
    }
}
