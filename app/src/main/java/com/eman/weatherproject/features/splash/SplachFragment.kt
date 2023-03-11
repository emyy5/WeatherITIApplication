package com.eman.weatherproject.features.splash

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.eman.weatherproject.R
import com.eman.weatherproject.RemoteSource
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.model.WeatherForecast
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.database.repository.RepositoryInterface
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.databinding.FragmentSplachBinding
import com.eman.weatherproject.utilities.*


class SplachFragment : Fragment() {

    private lateinit var binding: FragmentSplachBinding
    private lateinit var navController: NavController
    private var currentWeather: WeatherForecast? = null
    private var settings: Settings? = null
    private lateinit var repo: RepositoryInterface

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentSplachBinding.inflate(layoutInflater)
        navController = Navigation.findNavController(requireActivity(),R.id.nav_host_fragment)

        Handler().postDelayed({
            if(currentWeather == null){
                navController.navigate(R.id.action_splachFragment_to_initialFragment)
            }
            else{
                val action =
                    SplachFragmentDirections.actionSplachFragmentToHomeFragment(
                        currentWeather?.lat?.toFloat() as Float,
                        currentWeather?.lon?.toFloat() as Float,
                        units[settings?.unit as Int],
                        false
                    )
                navController.navigate(action)
                val toolBar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
                toolBar!!.visibility = View.VISIBLE
            }
        }, 2000)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(requireActivity()),
            requireContext(),
            requireContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE))
        currentWeather = repo.getWeatherSharedPreferences()
        settings = repo.getSettingsSharedPreferences()
        if(settings == null){
            this.settings = Settings(ENGLISH, STANDARD, NONE, ENABLED)
            repo.addSettingsToSharedPreferences(settings as Settings)
        }
        else{
            if(settings?.language as Boolean) {
                LocaleHelper.setLocale(requireContext(), "en")
            }
            else{
                LocaleHelper.setLocale(requireContext(), "ar")
            }
        }
    }

}