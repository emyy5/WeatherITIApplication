package com.eman.weatherproject.features.initial

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager

import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import java.util.*
import kotlin.math.log


private const val TAG = "fsojidkfl"
class InitialFragment : Fragment() {
    private val PERMISSION_ID = 100
    private lateinit var fusedLocation: FusedLocationProviderClient
    private lateinit var initialDialog: Dialog
    private lateinit var _navController: NavController
    var navController: NavController
        get() = _navController
        set(value) {
            _navController = value
        }
    private var connectivity: ConnectivityManager? = null
    private var info: NetworkInfo? = null

    private var settings: Settings? = null
    private lateinit var repo: Repository


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_initial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repo = Repository.getInstance(
            RemoteSource.getInstance(),
            LocalSource.getInstance(requireActivity()),
            requireContext(),
            requireContext().getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
        )
        settings = repo.getSettingsSharedPreferences()


        navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        fusedLocation = LocationServices.getFusedLocationProviderClient(requireContext())
        initialDialog = Dialog(requireContext())
        initialDialog.setContentView(R.layout.initial_dialog)

        initialDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val gpsLocation: RadioButton = initialDialog.findViewById(R.id.gpsLocation)
        val okBtn: Button = initialDialog.findViewById(R.id.initialSetupBtn)

        initialDialog.show()

        okBtn.setOnClickListener {
            connectivity =
                context?.getSystemService(Service.CONNECTIVITY_SERVICE) as ConnectivityManager

            if (connectivity != null) {
                info = connectivity!!.activeNetworkInfo
                if (info != null) {
                    if (info!!.state == NetworkInfo.State.CONNECTED) {
                        if (gpsLocation.isChecked) {
                            checkLocationPermission()

                        } else {
                            val action = InitialFragmentDirections.actionInitialFragmentToMapsFragment(true)
                            navController.navigate(action)
                        }
                    } else {
                        Toast.makeText(requireContext(), "Internet Connection", Toast.LENGTH_LONG).show()
                    }
                }
            }
            initialDialog.dismiss()
        }

    }

    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(context as Context,
            Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context as Context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val lm: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun enableLocationSettings() {
        val settingsIntent = Intent(ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(settingsIntent)
    }

    private fun requestLocationPermissions() {
        ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)

    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        Log.d(TAG, "requestNewLocationData: ")
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 5
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1
//        fusedLocation.requestLocationUpdates(locationRequest, locationCallBack, Looper.myLooper())

        fusedLocation.lastLocation.addOnCompleteListener(requireActivity()) { task ->
            try {
                val location: Location? = task.result
                Log.d(TAG, "setLocation location:  $location")
                if (location != null) {

                    settings?.location = 1
                    repo.addSettingsToSharedPreferences(settings as Settings)
                    val action = InitialFragmentDirections.actionInitialFragmentToHomeFragment(lat =location.latitude.toFloat(), loong = location.longitude.toFloat(), unit = units[settings?.unit as Int], comeForm = true)
                    navController.navigate(action)
                    val toolBar = activity?.findViewById<androidx.appcompat.widget.Toolbar>(R.id.mainToolbar)
                    toolBar!!.visibility = View.VISIBLE

                } else {
                    Log.d(TAG, "setLocation: null location")
                }
            } catch (e: Exception) {
                Log.d(TAG, "setLocation error: ${e.message}")
            }
        }
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder(requireContext())
                    .setTitle(getString(R.string.Location_Permission_Needed))
                    .setMessage(getString(R.string.This_app_needs_the_Location_permission_please_accept_to_use_location_functionality))
                    .setPositiveButton(R.string.ok) { _, _ ->
                        requestLocationPermission()
                    }.create().show()
            } else {
                requestLocationPermission()
            }
        } else {
            requestNewLocationData()
        }
    }

    private fun requestLocationPermission() {
        requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_ID -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        requestNewLocationData()
                    }
                } else {
                    checkLocationPermission()
                }
                return
            }
        }
    }


}