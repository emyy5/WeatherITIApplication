package com.eman.weatherproject.features.favourities.view

import android.app.AlertDialog
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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.eman.weatherproject.R
import com.eman.weatherproject.RemoteSource
import com.eman.weatherproject.database.model.WeatherAddress
import com.eman.weatherproject.database.model.WeatherForecast
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.database.room.FavClickInterface
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.databinding.FragmentFavouriteBinding
import com.eman.weatherproject.favourities.FavouroiteAdapter
import com.eman.weatherproject.features.favourities.viewmodel.FavoriteViewModelFactory
import com.eman.weatherproject.features.favourities.viewmodel.FavouriteViewModel
import com.eman.weatherproject.utilities.SHARED_PREFERENCES


class FavouriteFragment : Fragment(), FavClickInterface {

private lateinit var navController: NavController
private lateinit var favouroiteAdapter: FavouroiteAdapter
private lateinit var layoutManager: LinearLayoutManager
private lateinit var favouriteViewModel: FavouriteViewModel
private lateinit var favouriteViewModelFactory: FavoriteViewModelFactory
private lateinit var binding: FragmentFavouriteBinding
var connectivity: ConnectivityManager?=null
    var info: NetworkInfo?=null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_favourite, container, false)
    }

    fun addBtn(){
        binding.floatingAddFav.setOnClickListener {
            val action =
                FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment(
                    false
                )
            navController.navigate(action)
        }
    }

    override fun onRemoveBtnClick(address: WeatherAddress, weather: WeatherForecast) {
        val dialogBuilder = AlertDialog.Builder(requireContext())
        dialogBuilder.setMessage(getString(R.string.deleteMsg))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.delete)) { dialog, id ->
                favouriteViewModel.removeAddressFromFavorites(address)
                favouriteViewModel.removeOneFavWeather(weather)
                dialog.cancel()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialog, id -> dialog.cancel() }
        val alert = dialogBuilder.create()
        alert.show()
    }

    override fun onFavItemClick(address: WeatherAddress) {

        favouriteViewModel.getOneWeather(address.lat,address.lon).observe(viewLifecycleOwner) {
            if(it == null){
                Log.i("TAG", "No iteeeeem")
            }
            if(navController.currentDestination?.id == R.id.favouriteFragment) {
                val action =
                    FavouriteFragmentDirections.actionFavouriteFragmentToFavouriteDetailsFragment()
                navController.navigate(action)
            }
        }
    }

    private fun updateWeatherDatabase(){

        val observerName1 = Observer<List<WeatherAddress>> {
            for (favWeather in it){
                favouriteViewModel.getFavWholeWeather(favWeather.lat,favWeather.lon,"metric")

                val observerName2 = Observer<WeatherForecast> { item ->
                    favouriteViewModel.addOneFavWeather(item) }
                favouriteViewModel.favWeatherFromNetwork.observe(viewLifecycleOwner,observerName2)
            }
        }
        favouriteViewModel.getAllAddresses().observe(viewLifecycleOwner, observerName1)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentFavouriteBinding.bind(view)
        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        favouriteViewModelFactory = FavoriteViewModelFactory(
            Repository.getInstance(
                RemoteSource.getInstance(),
                LocalSource.getInstance(requireActivity()),
                requireContext(),
                requireContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
            )
        )

        favouriteViewModel = ViewModelProvider(this, favouriteViewModelFactory)[FavouriteViewModel::class.java]
        setupFavRecycler()
        addBtn()


        connectivity =
            context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (connectivity != null) {
            info = connectivity!!.activeNetworkInfo
            if (info != null) {
                if (info!!.state == NetworkInfo.State.CONNECTED) {
                    updateWeatherDatabase()
                } else {
                    binding.floatingAddFav.isEnabled = false
                }
            } else {
                binding.floatingAddFav.isEnabled = false
            }
        }

        val addressObserver = Observer<List<WeatherAddress>> {
            if(it != null){
                favouroiteAdapter.setFavAddressesList(it)
            }
            favouroiteAdapter.notifyDataSetChanged()
        }
        favouriteViewModel.getAllAddresses().observe(viewLifecycleOwner,addressObserver)

        val weatherObserver = Observer<List<WeatherForecast>> {
            if(it != null) {
                favouroiteAdapter.setFavWeatherList(it)
            }
            favouroiteAdapter.notifyDataSetChanged()
        }
        favouriteViewModel.getAllWeathersInVM().observe(viewLifecycleOwner,weatherObserver)

        binding.floatingAddFav.setOnClickListener {
            val action = FavouriteFragmentDirections.actionFavouriteFragmentToMapFragment(false)
            navController.navigate(action)
        }
    }


    fun setupFavRecycler(){
        favouroiteAdapter = FavouroiteAdapter(requireContext(), emptyList(),emptyList(),this)
        layoutManager = LinearLayoutManager(requireContext())
        binding.favoriteRecycler.adapter = favouroiteAdapter
        binding.favoriteRecycler.layoutManager = layoutManager
    }

}