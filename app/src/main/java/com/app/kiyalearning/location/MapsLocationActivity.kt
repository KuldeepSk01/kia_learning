package com.app.kiyalearning.location

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.*
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.app.kiyalearning.R
import com.app.kiyalearning.databinding.ActivityMapsLocationBinding
import com.app.kiyalearning.student.dashboard.DashBoardActivity
import com.app.kiyalearning.util.AppPref
import com.app.kiyalearning.util.UtilClass

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


class MapsLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var binding: ActivityMapsLocationBinding
    private var googleMap: GoogleMap? = null
    private var location: Location? = null
    var address: String? = null
    lateinit var locationManager: LocationManager


    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMapsLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        UtilClass.setStatusBarProperty(this)

//        val isCheckOut=intent.getBooleanExtra("IS_CHECK_OUT",false)
//        if(isCheckOut)
//            binding.confrmLocBtn.text=getString(R.string.check_out)

        val mLocationListener = LocationListener {
            location = it
            if (googleMap != null) {
                val mark = LatLng(location!!.latitude, location!!.longitude)
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(mark)
                        .title("My Location")
                )
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mark, 15f), 1000, null)
            }
            if (location != null) {
                binding.locLayout.visibility = View.VISIBLE
                address = getAddress(this, location!!.latitude, location!!.longitude)
                if (!address.isNullOrEmpty())
                    binding.locNameTxt.text = address
            }
        }

        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        locationManager.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            2000,
            100f,
            mLocationListener
        )

        binding.backLocationMaps.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        if (UtilClass.hasPlayServices(this)) {
            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }

        binding.confrmLocBtn.setOnClickListener {
            if (!address.isNullOrEmpty()) {
                AppPref.setLocName(binding.root.context, address!!)
                AppPref.setUserLat(binding.root.context, location!!.latitude.toString())
                AppPref.setUserLon(binding.root.context, location!!.longitude.toString())

                var intent = Intent(
                    binding.root.context,
                    com.app.kiyalearning.teacher.dashboard.DashBoardActivity::class.java
                )
                if (AppPref.getUserType(this) == "student")
                    intent = Intent(binding.root.context, DashBoardActivity::class.java)

                startActivity(intent)
                finishAffinity()

                /* if(isCheckOut)
                 {
                     binding.loader.pB.visibility = View.VISIBLE
                     val map = HashMap<String, Any>()
                     map["userid"] = AppPref.getUserId(this)
                     map["lat"] =   location!!.latitude.toString()

                     map["long"] =location!!.longitude.toString()
                     map["address"] = address!!
                     //   map["notification_token"] = AppPref.getFirebaseToken(this)
                     val api: Api = RestManager.getInstance()
                     val call: Call<CheckInResponse> = api.checkOut(map)
                     call.enqueue(object : Callback<CheckInResponse> {
                         override fun onResponse(call: Call<CheckInResponse>, response: Response<CheckInResponse>) {
                             binding.loader.pB.visibility = View.GONE
                             if (response.body() != null) {
                                 val checkInResponse:CheckInResponse= response.body()!!
                                 Toast.makeText(applicationContext, checkInResponse.message,Toast.LENGTH_LONG).show()

                                 if(!checkInResponse.address.isNullOrEmpty())
                                 {
                                     AppPref.setCheckOutLocName(binding.root.context, checkInResponse.address)
                                     //     AppPref.setChec(binding.root.context, checkInResponse.lat)
                                     //   AppPref.setUserLon(binding.root.context, checkInResponse.long)
                                     AppPref.setCheckOutTime(binding.root.context, checkInResponse.time)
                                     AppPref.setCheckOutDate(binding.root.context, checkInResponse.date)
                                 }
                                 Toast.makeText(applicationContext,"Check Out Done",Toast.LENGTH_SHORT).show()
                                 onBackPressed()
                             }else
                                 Toast.makeText(applicationContext, response.message(),Toast.LENGTH_LONG).show()
                         }
                         override fun onFailure(call: Call<CheckInResponse>, t: Throwable) {
                             binding.loader.pB.visibility = View.GONE
                             //when id not found
                             Toast.makeText(applicationContext,R.string.server_error,Toast.LENGTH_LONG).show()
                         }
                     })
                 }else
                 {
                         binding.loader.pB.visibility = View.VISIBLE
                         val map = HashMap<String, Any>()
                         map["userid"] = AppPref.getUserId(this)
                         map["lat"] =   location!!.latitude.toString()

                         map["long"] =location!!.longitude.toString()
                         map["address"] = address!!
                         //   map["notification_token"] = AppPref.getFirebaseToken(this)
                         val api: Api = RestManager.getInstance()
                         val call: Call<CheckInResponse> = api.checkIn(map)
                         call.enqueue(object : Callback<CheckInResponse> {
                             override fun onResponse(call: Call<CheckInResponse>, response: Response<CheckInResponse>) {
                                 binding.loader.pB.visibility = View.GONE
                                 if (response.body() != null) {
                                     val checkInResponse:CheckInResponse= response.body()!!
                                     Toast.makeText(applicationContext, checkInResponse.message,Toast.LENGTH_LONG).show()
                                     //return location update only
                                     if(!checkInResponse.address.isNullOrEmpty())
                                     {
                                     AppPref.setLocName(binding.root.context,checkInResponse.address)
                                     AppPref.setUserLat(binding.root.context, checkInResponse.lat)
                                     AppPref.setUserLon(binding.root.context, checkInResponse.long)
                                     AppPref.setCheckInTime(binding.root.context, checkInResponse.time)
                                     AppPref.setCheckInDate(binding.root.context, checkInResponse.date)
                                         }

                                     startActivity(Intent(binding.root.context, DashBoardActivity::class.java))
                                     finish()
                                 }else
                                     Toast.makeText(applicationContext, response.message(),Toast.LENGTH_LONG).show()
                             }
                             override fun onFailure(call: Call<CheckInResponse>, t: Throwable) {
                                 binding.loader.pB.visibility = View.GONE
                                 //when id not found
                                 Toast.makeText(applicationContext,R.string.server_error,Toast.LENGTH_LONG).show()
                             }
                         })
                 }*/

            } else
                Toast.makeText(this, "Problem Occur in Location", Toast.LENGTH_SHORT).show()
        }

        /*   locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
           locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER,500,50f, mLocationListener!!)*/

    }

    private fun gpsCheck() {
        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            showGPSDisabledAlertToUser()
        }
    }

    private fun showGPSDisabledAlertToUser() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage("GPS is disabled in your device. Please enable it")
            .setCancelable(false)
            .setPositiveButton("Ok") { _: DialogInterface?, _: Int ->
                val callGPSSettingIntent =
                    Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(callGPSSettingIntent)
            }
        val alert = alertDialogBuilder.create()
        alert.show()
    }

    override fun onBackPressed() {
        finish()
    }


    override fun onStart() {
        super.onStart()
        gpsCheck()
        val lRequest = LocationRequest.create()
        lRequest.interval = 60000
        lRequest.fastestInterval = 5000
        lRequest.priority = Priority.PRIORITY_BALANCED_POWER_ACCURACY
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0

        googleMap!!.isMyLocationEnabled = true
        googleMap!!.uiSettings.isMyLocationButtonEnabled = true

        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location1: Location? ->
            location = location1
            if (location != null) {
                binding.locLayout.visibility = View.VISIBLE
                address = getAddress(this, location!!.latitude, location!!.longitude)
                if (!address.isNullOrEmpty())
                    binding.locNameTxt.text = address

                val mark = LatLng(location!!.latitude, location!!.longitude)
                googleMap!!.addMarker(
                    MarkerOptions()
                        .position(mark)
                        .title("My Location")
                )
                googleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(mark, 15f), 1000, null)
            }
        }
    }

    private fun getAddress(context: Context, lat: Double, lng: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)!!
            val obj: Address = addresses[0]
            val add: String = obj.getAddressLine(0)
            add
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            null
        }
    }
}