package com.app.finalmaybe

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import org.json.JSONObject
import java.io.DataInputStream
import java.io.File
import java.io.FileInputStream

class PlotActivity: AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var geocoder: Geocoder
    private lateinit var currentLocation: Location
    private lateinit var marker: Marker
    val environme =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()
            .toString()
    private var current: Triple<Double,Double,String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.plot_view)

        var Tripster = application as Tripster
        val directoryz: File = File(environme + "/Tripster/${Tripster.currentFile}/maps")

        mapView = findViewById(R.id.plot_view)
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        geocoder = Geocoder(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        println("MakingMap")
        map = googleMap
        map.isIndoorEnabled
        map.isTrafficEnabled
        map.uiSettings
        enableMyLocation()
        Log.d("map?", map.toString())
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
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            if (location != null) {
                currentLocation = location
                current = Triple(location.latitude,location.longitude,"Current Location")
                val currentLatLong = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
            }
        }
        searchLocation()
    }

    private fun enableMyLocation(): Triple<Double, Double, String>{
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location != null) {
                    currentLocation = location
                    current = Triple(location.latitude,location.longitude,"Current Location")
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
                }
            }
        }
        return current ?: Triple(0.0, 0.0, "")
    }

    private fun searchLocation() {

        println("ATTEMPTING TO SEARCH")

        if (::marker.isInitialized) {
            println(marker.title)
            map.clear()
        }
        var current = enableMyLocation()
        println(current)
        for (items in readContents()) {
            if (current != null && current.first != items.first){
                Log.d("WHAT IS MY LOCATION EXACLY?","${current.first}")
                val path: MutableList<List<LatLng>> = ArrayList()
                val urlDirections = "https://maps.googleapis.com/maps/api/directions/json?origin=" +
                        "${current.first},${current.second}&destination=${items.first},${items.second}" +
                        "&key=INSERTKEYHERE"
                val directionsRequest = object : StringRequest(Request.Method.GET, urlDirections, Response.Listener<String> {
                        response ->
                    val jsonResponse = JSONObject(response)
                    // Get routes

                    val routes = jsonResponse.getJSONArray("routes")
                    if (routes != null && routes.length() > 0) {
                        val legs = routes.getJSONObject(0).getJSONArray("legs")
                        val steps = legs.getJSONObject(0).getJSONArray("steps")
                        for (i in 0 until steps.length()) {
                            val points = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                            path.add(PolyUtil.decode(points))
                        }
                    }
                    for (i in 0 until path.size) {
                        map!!.addPolyline(PolylineOptions().addAll(path[i]).color(Color.RED))
                    }
                }, Response.ErrorListener {
                        _ ->
                }){}
                val requestQueue = Volley.newRequestQueue(this)
                requestQueue.add(directionsRequest)

            }
            current = items


            val address = geocoder.getFromLocation(items.first, items.second, 1)?.get(0)
            var latLng = LatLng(address!!.latitude, address!!.longitude)
            marker = map.addMarker(MarkerOptions().position(latLng).title(items.third))!!
            marker.showInfoWindow()
        }
    }


    private fun readContents(): List<Triple<Double, Double, String>> {
        val tripster = application as Tripster
        val currentFile = tripster.currentFile
        val environme =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()
                .toString()
        val directory = File(environme + "/Tripster/${currentFile}/maps")
        println(directory)
        Log.d("THIS IS A FLAG", directory.toString())
        val doubleList = mutableListOf<Triple<Double, Double, String>>()

        if (directory.isDirectory) {
            directory.listFiles()?.forEach { file ->
                if (file.isFile && file.extension == "dat") {
                    var inputStream = FileInputStream(file.absolutePath).buffered()
                    var dataInputStream = DataInputStream(inputStream)

                    var firstDouble = dataInputStream.readDouble()
                    var secondDouble = dataInputStream.readDouble()


                    println(firstDouble)
                    doubleList.add(Triple(firstDouble, secondDouble, file.name))


                }
                else {
                    println("Directory not found")
                }
                println(doubleList)

            }

        }
        return (doubleList)
    }
}