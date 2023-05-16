package com.app.finalmaybe

import GoogleSearchApi
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.barteksc.pdfviewer.PDFView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mancj.materialsearchbar.MaterialSearchBar
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.File


class PdfActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView

    //private lateinit var btnStore: Button
    //private lateinit var btnDelete: Button
    private lateinit var saveButton: Button
    private lateinit var btnSearch: Button
    private lateinit var openlocation: Button
    private lateinit var etSearchQuery: MaterialSearchBar
    private var searchResultUrl: String? = null
    lateinit var myAPI: SuggestionAdapter
    var compositeDisposable = CompositeDisposable()
    var suggestions: MutableList<String> = ArrayList()


    private val googleSearchApi =
        GoogleSearchApi("APIKEYHERE", "CX")
    val environme =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getPath()
            .toString()


    private val pdfFileName = "sample.pdf"
    private val storagePermissions = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )
    private val manageExternalStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Permission granted, proceed with your operation
                println("GRANTED")
            } else {
                // Permission denied, show an error message or request again
                println("DENIED")
                requestPermissions(
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    5
                )
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val Tripster = application as Tripster
        var directoryz: File = File(environme + "/Tripster/${Tripster.currentFile}/pdfs")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // Permission not granted, request it
                manageExternalStoragePermission.launch(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                )
            }
        }


        if (!directoryz.exists()) {
            directoryz.mkdir()
            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())
        } else {
            Log.d("WHEREDOISAVE", directoryz.absolutePath.toString())
        }
        setContentView(R.layout.activity_pdf)
        pdfView = findViewById(R.id.pdfView)

        //btnStore = findViewById(R.id.btn_store)

        checkPermissions()

        openlocation = findViewById(R.id.gallery)
        btnSearch = findViewById(R.id.btn_search)
        etSearchQuery = findViewById(R.id.et_search_query)
        saveButton = findViewById(R.id.save_pdf_button)

        openlocation.setOnClickListener {

            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .getPath().toString() + "/Tripster/pdfs"
            )
            val intents = Intent(this, FileListActivity::class.java)
            //intents.setDataAndType(Uri.fromFile(file),"*")

            startActivity(intents)
        }
        etSearchQuery.addTextChangeListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                getSuggestText(p0.toString(),
                "chrome",
                "en",
                "")
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })


        etSearchQuery.setOnSearchActionListener(object:MaterialSearchBar.OnSearchActionListener{
            override fun onSearchStateChanged(enabled: Boolean) {
                Log.d("PdfActivity", "Search state changed: $enabled")
            }

            override fun onSearchConfirmed(text: CharSequence?) {

                println(etSearchQuery.text)
                searchPdf(etSearchQuery.text)
            }

            override fun onButtonClicked(ButtonCode: Int){
            searchPdf(etSearchQuery.text)
        }
    })
        myAPI = RetrofitClient.instance.create(SuggestionAdapter::class.java)
        btnSearch.setOnClickListener {
            searchPdf(etSearchQuery.text)
        }

        saveButton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                saveButton.isEnabled = true
                println("STARKRAVINGMAD")
                searchResultUrl?.let { it1 -> savePdfFromUrl(it1) }


            } else {searchResultUrl?.let { it1 -> savePdfFromUrl(it1) }
                println("TRYINGTOGETPERMISSIONS")
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    4
                )
            }

        }
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
    private fun searchPDFs(query: String){

        CoroutineScope(Dispatchers.Main).launch{
            val searchResults = withContext(Dispatchers.IO){googleSearchApi.search(query)}
            if (searchResults.isNotEmpty()){
                val results = searchResults.listIterator()
                val pdfLinks = mutableListOf<String>()
                while (results.hasNext()) {
                    val result = results.next()
                    result.link
                    }
                }
            }
        }

    private fun searchPdf(query: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val searchResults = withContext(Dispatchers.IO) { googleSearchApi.search(query) }
            if (searchResults.isNotEmpty()) {
                val result = searchResults.first()
                searchResultUrl = result.link
                loadPdfFromUrl(result.link)

            } else {
                Toast.makeText(this@PdfActivity, "No Result Found", Toast.LENGTH_SHORT).show()
            }

        }
    }
    private fun loadPdfFromUrl(url: String){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        CoroutineScope(Dispatchers.Main).launch {
            val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
            if (response.isSuccessful) {
                response.body?.byteStream()?.use { inputStream ->
                    val mk = File.createTempFile("holder",".pdf", cacheDir)
                    mk.outputStream().use { outputStream ->
                        inputStream.copyTo(outputStream)
                    }
                    pdfView.fromFile(mk).defaultPage(0).spacing(10).load()
                    mk.deleteOnExit()
                }
            } else {
                Toast.makeText(this@PdfActivity, "Failed to load PDF", Toast.LENGTH_SHORT)
                    .show()
                }
            }
        }

    private fun savePdfFromUrl(url: String) {
        println("STARKRAVINGMAD")
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        val Tripster = application as Tripster
        var directoryz: File = File(environme + "/Tripster/${Tripster.currentFile}/pdfs")
        CoroutineScope(Dispatchers.Main).launch {
                val response = withContext(Dispatchers.IO) { client.newCall(request).execute() }
                println(directoryz.toString())
                File(directoryz.toString()).walk().forEach {
                    println(it)
                }

                if (response.isSuccessful) {

                    response.body?.byteStream()?.use { inputStream ->
                        val directorate = File("$directoryz")
                        val targets = etSearchQuery.text + ".pdf"
                        val nef = File(directorate,targets)
                        nef.createNewFile()
                        nef.outputStream().use { outputStream ->
                            inputStream.copyTo(outputStream)
                        }
                        pdfView.fromFile(nef).defaultPage(0).spacing(10).load()

                    }
                } else {
                    Toast.makeText(this@PdfActivity, "Failed to load PDF", Toast.LENGTH_SHORT)
                        .show()
                }

        }
    }


    fun deleteDirectory(directory: File) {
        for (file in directory.listFiles()) {
            if (file.isDirectory) {
                deleteDirectory(file)
            } else {
                file.delete()
            }
        }
    }
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    storagePermissions,
                    1
                )
            }
        }
    }
    private fun storePdf(url: String) {
        val pdfFile = getPdfFile()
        if (!pdfFile.exists()) {
            searchResultUrl?.let { url ->
                savePdfFromUrl(url)
                val savedPdfsDir = File(getExternalFilesDir(null), "saved_pdfs")
                savedPdfsDir.mkdirs()
                val savedPdfFile = File(savedPdfsDir, pdfFile.name)
                if (pdfFile.exists()) {
                    pdfFile.copyTo(savedPdfFile, overwrite = true)
                } else {
                    Toast.makeText(this, "Error: PDF file does not exist", Toast.LENGTH_SHORT).show()
                }
            } ?: run {
                Toast.makeText(this, "No PDF to store", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun getPdfFile(): File {
        val filesDir = getFilesDir()
        return File(filesDir, pdfFileName)
    }
    private fun getSuggestText(query:String, client:String, language: String, restrict:String){
        if(TextUtils.isEmpty(restrict)){
            compositeDisposable.add(
                myAPI.getSuggestFromGoogle(query,client,language,restrict)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ string ->
                        if(suggestions.size>0) suggestions.clear()
                        val json_array = JSONArray(string)
                        suggestions = Gson().fromJson<List<String>>(json_array.getString(1),
                        object: TypeToken<List<String>>(){}.type).toMutableList()

                        etSearchQuery.updateLastSuggestions(suggestions)
                    },
                {throwable -> Toast.makeText(this@PdfActivity,""+throwable.message,Toast.LENGTH_LONG).show()}
            ))
        }
        else{
            compositeDisposable.add(
                myAPI.getSuggestFromMaps(query,client,language,restrict)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ string ->
                        if(suggestions.size>0) suggestions.clear()
                        val json_array = JSONArray(string)
                        suggestions = Gson().fromJson<List<String>>(json_array.getString(1),
                            object: TypeToken<List<String>>(){}.type).toMutableList()

                        etSearchQuery.updateLastSuggestions(suggestions)
                    },
                        {throwable -> Toast.makeText(this@PdfActivity,""+throwable.message,Toast.LENGTH_LONG).show()}))

        }

    }
}
