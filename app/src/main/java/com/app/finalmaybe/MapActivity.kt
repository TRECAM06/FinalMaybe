package com.app.finalmaybe

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.mancj.materialsearchbar.MaterialSearchBar
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


class MapActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var LSearchQuery: MaterialSearchBar
    private var searchResultUrl: String? = null
    private lateinit var geocoder: Geocoder
    private lateinit var saver: Button
    private lateinit var currentLocation: Location
    private lateinit var searchMap: SearchView

    private lateinit var searchedLatLng: List<Double>
    private lateinit var LbtnSearch: Button
    private lateinit var marker:Marker

    private val AUTOCOMPLETE_REQUEST_CODE = 1
    val environme = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().toString()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        var Tripster = application as Tripster
        val directoryz: File = File(environme + "/Tripster/${Tripster.currentFile}/maps")
        LbtnSearch = findViewById(R.id.Lbtn_search)
        LSearchQuery = findViewById(R.id.lsearch_query)
        LSearchQuery.setOnSearchActionListener(object:MaterialSearchBar.OnSearchActionListener{
            override fun onSearchStateChanged(enabled: Boolean) {
                println("Test")
            }

            override fun onSearchConfirmed(text: CharSequence?) {
                searchLocation(LSearchQuery.text)
            }

            override fun onButtonClicked(buttonCode: Int) {
                searchLocation(LSearchQuery.text)
            }

        })
        LbtnSearch.setOnClickListener {
            searchLocation(LSearchQuery.text)
        }
        if (!directoryz.exists()) {
            directoryz.mkdir()
            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())
        } else {
            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())
        }

        mapView = findViewById(R.id.map_view)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geocoder = Geocoder(this)

        val saver = findViewById<Button>(R.id.save_location)
        saver.setOnClickListener{
            saveLocation()
        }
        val location_open =findViewById<Button>(R.id.Location_List_Button)
        location_open.setOnClickListener{
            val intent = Intent(this, LocationListActivity::class.java)
            startActivity(intent)
        }

    }



    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        enableMyLocation()
        map.isIndoorEnabled
        map.isTrafficEnabled
        map.uiSettings
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        map.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener(this) {
            location -> if(location != null){
                currentLocation = location
                val currentLatLong = LatLng(location.latitude,location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong,12f))
        }
        }

    }

    private fun enableMyLocation() {
        val fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
        // 1. Check if permissions are granted, if so, enable the my location layer
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            return
        }
    }
    private fun searchLocation(query: String) {

        try {
            val addressList = geocoder.getFromLocationName(query, 1)
            if (addressList != null) {
                if (addressList.isNotEmpty()) {
                    val address = addressList?.get(0)
                    val latLng = LatLng(address!!.latitude, address!!.longitude)
                    searchedLatLng = listOf(address!!.latitude,address!!.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))
                    if(::marker.isInitialized){
                        println(marker.title)
                        map.clear();
                    }
                    Thread.sleep(300)
                    marker = map.addMarker(MarkerOptions().position(latLng).title(query))!!
                    marker!!.showInfoWindow()
                } else {
                    Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: IOException) {
            Toast.makeText(this, "Error searching location", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveLocation() {
        Log.d("Where Are We?","$searchedLatLng")
        var Tripster = application as Tripster
        val directoryz: File = File(environme + "/Tripster/${Tripster.currentFile}/maps")
            var newlocation = searchedLatLng
            if (searchedLatLng != null) {
                //val parts = listOf(location.latitude," , ",location.longitude)
                Log.d("Where Are We?","$searchedLatLng")
                val directorate = File("$directoryz")
                val targets = findViewById<MaterialSearchBar>(R.id.lsearch_query).text.toString() + ".dat"
                val nef = File(directorate, targets)
                nef.createNewFile()
                val output = DataOutputStream(FileOutputStream("${nef.absolutePath}"))
                println(searchedLatLng[0] + 2000.0115)
                output.writeDouble(searchedLatLng[0] )
                output.writeDouble(searchedLatLng[1] )
                output.close()



        }}


    override fun onResume() {
            super.onResume()
            mapView.onResume()
        }

        override fun onPause() {
            super.onPause()
            mapView.onPause()
        }

        override fun onDestroy() {
            super.onDestroy()
            mapView.onDestroy()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mapView.onLowMemory()
        }

}



