package com.example.carmanager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapActivity : FragmentActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    private var marker: Marker?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)



        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


        findViewById<Button>(R.id.btn_back).setOnClickListener {
            finish()
        }


        findViewById<Button>(R.id.btn_save_location).setOnClickListener {
            saveCurrentLocation()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
            return
        }
        mMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                lastLocation = it
                val currentLocation = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))

                loadSavedLocation()
            }
        }
    }

    private fun saveCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                lastLocation = it
                val currentLocation = LatLng(it.latitude, it.longitude)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f))
                marker?.remove()
                marker=mMap.addMarker(MarkerOptions().position(LatLng(lastLocation.latitude,lastLocation.longitude)).title("Zaparkowany samochód"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lastLocation.latitude,lastLocation.longitude), 15f))
                val sharedPreferences = getSharedPreferences("PARKING_PREFS", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("LATITUDE", lastLocation.latitude.toString())
                editor.putString("LONGITUDE", lastLocation.longitude.toString())
                editor.apply()
                Toast.makeText(this, "Lokalizacja samochodu zapisana!", Toast.LENGTH_SHORT).show()

            }
        }


    }

    private fun loadSavedLocation() {
        val sharedPreferences = getSharedPreferences("PARKING_PREFS", Context.MODE_PRIVATE)
        val latitude = sharedPreferences.getString("LATITUDE", null)
        val longitude = sharedPreferences.getString("LONGITUDE", null)

        if (latitude != null && longitude != null) {
            val savedLocation = LatLng(latitude.toDouble(), longitude.toDouble())

           marker= mMap.addMarker(MarkerOptions().position(savedLocation).title("Zaparkowany samochód"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(savedLocation, 15f))
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            onMapReady(mMap)
        }
    }
}


