package com.app.finalmaybe

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var buttonLayout: LinearLayout
    private lateinit var tripRecyclerview: RecyclerView
    val environme = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().toString()
    val directoryz = File(environme+"/Tripster")



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val Tripster = application as Tripster
        val currentFile = Tripster.currentFile
        if (!directoryz.exists()) {
            directoryz.mkdir()

            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())
        } else {
            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())

        }
        Thread.sleep(800)
        if (!File(environme+"/Tripster/${Tripster.currentFile}").exists()){
            File(environme+"/Tripster/${Tripster.currentFile}").mkdir()
            File(environme+"/Tripster/${Tripster.currentFile}/pdfs").mkdir()
            File(environme+"/Tripster/${Tripster.currentFile}/maps").mkdir()
        }

        val mapActivityButton = findViewById<Button>(R.id.map_activity_btn)
        val pdfActivityButton = findViewById<Button>(R.id.pdf_activity_btn)
        val settingsActivityButton = findViewById<Button>(R.id.settings)
        //val recyclerView = findViewById<RecyclerView>(R.id.list_plans_Recycler)
        val searchView = findViewById<SearchView>(R.id.search_trips)
        buttonLayout = findViewById(R.id.add_trip_holder)
        var listOfTrips = arrayListOf<String>(

        )
        tripRecyclerview = findViewById(R.id.list_plans_Recycler)
        tripRecyclerview.layoutManager = LinearLayoutManager(this)

        val root = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().toString()+"/Tripster")
        if (root?.isDirectory == true) {
            val filesAndFolders = root.listFiles()
            tripRecyclerview.adapter = TripAdapter(this, filesAndFolders ?: emptyArray())
        } else {
            Toast.makeText(this, "Unable to load files", Toast.LENGTH_SHORT).show()

        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                val Tripster = application as Tripster
                Tripster.currentFile = (searchView.query).toString()
                Log.d("NEW CURRENT FILE","${Tripster.currentFile}")
                if (!File(environme+"/Tripster/${Tripster.currentFile}").exists()){
                    File(environme+"/Tripster/${Tripster.currentFile}").mkdir()
                    File(environme+"/Tripster/${Tripster.currentFile}/pdfs").mkdir()
                    File(environme+"/Tripster/${Tripster.currentFile}/maps").mkdir()

                    val intent = intent
                    finish()
                    startActivity(intent)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Log.d("ADDED", "${searchView.query}")
                for (element in listOfTrips){
                    if (element.contains(searchView.query)){
                        return false
                    }
                }
                return false
            }
        })

        mapActivityButton.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)

        }
        pdfActivityButton.setOnClickListener {
            val intent = Intent(this, PdfActivity::class.java)
            startActivity(intent)

        }
        settingsActivityButton.setOnClickListener {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)

        }
    }

}



