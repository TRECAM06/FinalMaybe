package com.app.finalmaybe

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

lateinit var Recyclerview2: RecyclerView
lateinit var text2: TextView

private lateinit var plotButton: Button

// This code creates a list of locations from elements inside of the provided root folder.
class LocationListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_list)

        plotButton = findViewById(R.id.access_plot)
        plotButton.setOnClickListener {
            val intent = Intent(this, PlotActivity::class.java)
            startActivity(intent)

        }
        Recyclerview2 = findViewById(R.id.location_recycler_view)
        text2 = findViewById(R.id.location_textview)
        val Tripster = application as Tripster
        val currentFile = Tripster.currentFile
        val path = intent.getStringExtra("")
        val root = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().toString()+"/Tripster/${currentFile}/maps")
        val filesAndFolders = root.listFiles()


        Recyclerview2.layoutManager = LinearLayoutManager(this)
        Recyclerview2.adapter = LocationAdapter(applicationContext, filesAndFolders)

    }
}