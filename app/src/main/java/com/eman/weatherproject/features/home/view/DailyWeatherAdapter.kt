package com.eman.weatherproject.features.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.eman.weatherproject.database.model.DailyWeather
import com.eman.weatherproject.R
import com.eman.weatherproject.utilities.Converters

class DailyWeatherAdapter : RecyclerView.Adapter<DailyWeatherAdapter.DailyWeatherViewHolder> {

    private var dailyWeather: List<DailyWeather>
    private val myUnit: String
    private var context: Context

    constructor(context: Context, dailyWeather: List<DailyWeather>, tempUnit: String) {
        this.context = context
        this.dailyWeather = dailyWeather
        this.myUnit = tempUnit
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.daily_item_layout, parent, false)
        return DailyWeatherViewHolder(view)
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val oneDailyWeather: DailyWeather = dailyWeather[position]
        holder.dailyDate.text = Converters.getDayFormat(oneDailyWeather.dt)
        holder.dailyDesc.text = oneDailyWeather.weather[0].description
        holder.dailyTemp.text = "${oneDailyWeather.temp.max}/${oneDailyWeather.temp.min}"
        when (this.myUnit) {
            "standard" -> {
                holder.dailyUnit.text = context.getString(R.string.Kelvin)
            }
            "metric" -> {
                holder.dailyUnit.text = context.getString(R.string.Celsius)
            }
            "imperial" -> {
                holder.dailyUnit.text = context.getString(R.string.Fahrenheit)
            }
        }

        Glide.with(context)
            .load("https://openweathermap.org/img/wn/" + oneDailyWeather.weather[0].icon + "@2x.png")
            .into(holder.dailyIcon)
        //holder.dailyIcon.setImageResource(R.drawable.daily_icon)
    }

    override fun getItemCount(): Int {
        return dailyWeather.size - 1
    }

    fun setDailyWeatherList(dailyWeatherList: List<DailyWeather>) {
        this.dailyWeather = dailyWeatherList
    }

    inner class DailyWeatherViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val dailyDate: TextView get() = view.findViewById(R.id.dailyDate)
        val dailyDesc: TextView get() = view.findViewById(R.id.dailyDesc)
        val dailyTemp: TextView get() = view.findViewById(R.id.dailyTemp)
        val dailyIcon: ImageView get() = view.findViewById(R.id.dailyIcon)
        val dailyUnit: TextView get() = view.findViewById(R.id.dailyUnit)
    }
}