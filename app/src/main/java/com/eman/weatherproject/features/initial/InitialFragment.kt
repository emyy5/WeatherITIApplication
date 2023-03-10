package com.eman.weatherproject.features.initial

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Location
import android.location.LocationManager

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.eman.weatherproject.database.room.LocalSource
import com.eman.weatherproject.R
import com.eman.weatherproject.RemoteSource
import com.eman.weatherproject.database.model.Settings
import com.eman.weatherproject.database.repository.Repository
import com.eman.weatherproject.utilities.SHARED_PREFERENCES
import com.eman.weatherproject.utilities.units
import com.google.android.gms.location.*


class InitialFragment : Fragment() {
    val PERMISSION_ID = 100
    lateinit var fusedLocation: FusedLocationProviderClient
    lateinit var initialDialog: Dialog
    lateinit var navController: NavController
    var connectivity : ConnectivityManager? = null
    var info : NetworkInfo? = null

    private var settings: Settings? = null
    private lateinit var repo: Repository

    val locationCallBack = object : LocationCallback(){
        override fun onLocationResult(myLocation: LocationResult?) {
            super.onLocationResult(myLocation)
            var loc = myLocation?.lastLocation as Location
            settings?.location = 1
            repo.addSettingsToSharedPreferences(settings as Settings)

            val action =
               InitialFragmentDirections.actionInitialFragmentToHomeFragment(
                    loc.latitude.toFloat(), loc.longitude.toFloat(),
                    units[settings?.unit as Int], true
                )
            navController.navigate(action)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_initial, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(requireActivity()),
            requireContext(),
            requireContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE))
        settings = repo.getSettingsSharedPreferences()


        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        initialDialog = Dialog(requireContext())
        initialDialog.setContentView(R.layout.initial_dialog)

        initialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        var gpsLocation: RadioButton = initialDialog.findViewById(R.id.gpsLocation)
        var okBtn: Button = initialDialog.findViewById(R.id.initialSetupBtn)

        initialDialog.show()

        okBtn.setOnClickListener {
            connectivity = context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

            if ( connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {
                        if(gpsLocation.isChecked) {
                            getFreshLocationRequest()

                        }
                        else{
                            val action =
                                InitialFragmentDirections.actionInitialFragmentToMapsFragment(
                                    true
                                )
                            navController.navigate(action)
                        }
                    }
                    else{
                        Toast.makeText(requireContext(), "Internet Connection", Toast.LENGTH_LONG).show()
                    }
                }
            }
            initialDialog.dismiss()
        }

    }

    fun checkPermissions():Boolean{
        Log.i("TAG", "checkPermissions: ")
        return ActivityCompat.checkSelfPermission(context as Context,
            android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context as Context,
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    fun isLocationEnabled():Boolean{
        var lm: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    fun enableLocationSettings(){
        var settingsIntent = Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

    fun requestLocationPermissions(){
        requestPermissions(arrayOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION),
            PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == PERMISSION_ID){
            if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                requestNewLocationData()
            }
            else{
                Toast.makeText(context as Context, "your permission Refused", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestNewLocationData(){
        val locationRequest = LocationRequest.create()
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        locationRequest.setInterval(5)
        locationRequest.setFastestInterval(0)
        locationRequest.setNumUpdates(1)

        fusedLocation.requestLocationUpdates(locationRequest,locationCallBack, Looper.myLooper())
    }

    fun getFreshLocationRequest(){
        if(checkPermissions()) {
            if(isLocationEnabled()) {
                requestNewLocationData();
            }
            else{
                enableLocationSettings();
            }
        }
        else{

            Toast.makeText(activity, "There is no Permission", Toast.LENGTH_SHORT).show();
            requestLocationPermissions()
        }
    }


}