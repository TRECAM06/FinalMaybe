package com.app.finalmaybe

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textview.MaterialTextView
import java.io.File

lateinit var Recyclerview: RecyclerView
lateinit var text: MaterialTextView


class FileListActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_file_list)

        Recyclerview = findViewById(R.id.recycler_view)
        text = findViewById(R.id.file_textview)
        val Tripster = application as Tripster
        val currentFile = Tripster.currentFile

        val root = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath().toString()+"/Tripster/${currentFile}/pdfs")
        val filesAndFolders = root.listFiles()

        Recyclerview.layoutManager = LinearLayoutManager(this)
        Recyclerview.adapter = MyAdapter(applicationContext, filesAndFolders)



    }
}