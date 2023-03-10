package com.eman.weatherproject.features.home.view

import android.app.Service
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.airbnb.lottie.LottieAnimationView
import com.bumptech.glide.Glide
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.features.home.viewmodel.HomeViewModel
import com.eman.weatherproject.features.home.viewmodel.HomeViewModelFactory
import com.eman.weatherproject.R
import com.eman.weatherproject.RemoteSource
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.model.WeatherForecast
import com.eman.weatherproject.databinding.FragmentHomeBinding
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.utilities.Converters
import com.eman.weatherproject.utilities.SHARED_PREFERENCES
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    lateinit var animLoading: LottieAnimationView
    lateinit var viewModelFactory: HomeViewModelFactory
    lateinit var viewModel: HomeViewModel
    var info: NetworkInfo? = null
    lateinit var hourlyAdapter: HourlyWeatherAdapter
    lateinit var dailyAdapter: DailyWeatherAdapter
    lateinit var layoutManagerHourly: LinearLayoutManager
    lateinit var layoutManagerDaily: LinearLayoutManager
    lateinit var binding: FragmentHomeBinding
    var connectivity: ConnectivityManager? = null
    val locationArgs: HomeFragmentArgs by navArgs()
    private var settings: Settings? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)
        viewModelFactory = HomeViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireActivity()),
                requireActivity(),
                requireActivity().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
            )
        )
        viewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        animLoading = view.findViewById(R.id.animationView)
        settings = viewModel.getStoredSettings()

        setupRecyclerViews()

        if (viewModel.getStoredCurrentWeather() == null || locationArgs.comeForm) {
            viewModel.getWholeWeather(
                locationArgs.lat.toDouble(),
                locationArgs.loong.toDouble(),
                locationArgs.unit
            )

            viewModel.weatherFromNetwork.observe(viewLifecycleOwner) {
                if (it != null) {
                    applyUIChange(it)
                    viewModel.addWeatherInVM(it)
                }
                hourlyAdapter.notifyDataSetChanged()
                dailyAdapter.notifyDataSetChanged()
            }

        } else {

            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager
            if (connectivity != null) {
                info = connectivity!!.activeNetworkInfo

                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {

                        viewModel.updateWeatherPrefs(this)
                    }
                } else {
                    Log.i("TAG", "no information found ")
                }
            }
            applyUIChange(viewModel.getStoredCurrentWeather())

            hourlyAdapter.notifyDataSetChanged()
            dailyAdapter.notifyDataSetChanged()
        }

        lifecycleScope.launch {
            applyUIChange(
                viewModel.repo.getCurrentWeatherWithLocationInRepo(30.0444, 31.2357, "metric")
            )

            hourlyAdapter.notifyDataSetChanged()
            dailyAdapter.notifyDataSetChanged()
        }

    }

    fun setupRecyclerViews() {
        hourlyAdapter = HourlyWeatherAdapter(context as Context, arrayListOf(), "metric")
        dailyAdapter = DailyWeatherAdapter(context as Context, arrayListOf(), "metric")
        layoutManagerHourly =
            LinearLayoutManager(context as Context, LinearLayoutManager.HORIZONTAL, false)
        layoutManagerDaily = LinearLayoutManager(context as Context)
        binding.hourlyRecycler.adapter = hourlyAdapter
        binding.dailyRecycler.adapter = dailyAdapter
        binding.hourlyRecycler.layoutManager = layoutManagerHourly
        binding.dailyRecycler.layoutManager = layoutManagerDaily
    }


    fun applyUIChange(currWeather: WeatherForecast?) {
        currWeather as WeatherForecast
        animLoading.visibility = View.GONE
        binding.currCity.text = currWeather.timezone
        binding.currDate.text = Converters.getDateFormat(currWeather.current.dt)
        binding.currTime.text = Converters.getTimeFormat(currWeather.current.dt)
        binding.currTemp.text = currWeather.current.temp.toString()
        binding.currDesc.text = currWeather.current.weather[0].description
        binding.currHumidity.text = currWeather.current.humidity.toString()
        binding.currWindSpeed.text = currWeather.current.wind_speed.toString()
        binding.currClouds.text = currWeather.current.clouds.toString()
        binding.currPressure.text = currWeather.current.pressure.toString()
        binding.currUnit.text = getString(R.string.Kelvin)
        binding.currWindUnit.text = getString(R.string.windMeter)

        Glide.with(context as Context)
            .load("https://openweathermap.org/img/wn/" + currWeather.current.weather[0].icon + "@2x.png")
            .into(binding.currIcon)
        hourlyAdapter.setHourlyWeatherList(currWeather.hourly)
        dailyAdapter.setDailyWeatherList(currWeather.daily)
    }
}




