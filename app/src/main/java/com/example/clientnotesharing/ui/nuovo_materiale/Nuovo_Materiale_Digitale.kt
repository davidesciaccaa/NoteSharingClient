package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class Nuovo_Materiale_Digitale: AppCompatActivity() {
    private var nrPdfCaricati = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_materiale_digitale)
        // Access the SupportActionBar
        supportActionBar?.apply {
            title = getString(R.string.titolo_appbar_nuovo_materiale_digitale)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        val btnSelezionaPDF = findViewById<Button>(R.id.btnSelezionaPDF)
        val editTAnno = findViewById<EditText>(R.id.editTextNumberAnno)
        val editMultilineDescr = findViewById<EditText>(R.id.editTextTextMultiLineDescrizione)
        val editTCorso = findViewById<EditText>(R.id.editTextCorso)
        val buttonConferma = findViewById<Button>(R.id.btnCreaNuovoA)
        val buttonIndietro = findViewById<Button>(R.id.btnIndietro)

        btnSelezionaPDF.setOnClickListener {
            pickPdfFiles.launch("application/pdf") //per selezionare solo pdf
        }

        buttonConferma.setOnClickListener{
            val nuovoA = intent.getStringExtra("nuovoA").let {
                Json.decodeFromString<Annuncio>(it!!)
            }
            val nuovoMD = MaterialeDigitale(
                nuovoA.id,
                editTAnno.text.toString().toInt(),
                editTCorso.text.toString(),
                editMultilineDescr.text.toString()
            )
            lifecycleScope.launch {
                NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                NotesApi.retrofitService.uploadMaterialeDigitale(nuovoMD)
            }

            //to do: chiudere la pagina Nuovo annuncio
            //to do: controllo che non sono rimasti vuoti
        }
        buttonIndietro.setOnClickListener{
            onBackPressedDispatcher.onBackPressed() //clicca il back button
        }
    }

    private val pickPdfFiles = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        println("****************** uri: $uri")
        // Handle the returned Uri
        if (uri != null) {
            //val file = File(uri.path) // convert Uri to File
            val filePath = uriToFilePath(this, uri)
            if (filePath != null) {
                val file = File(filePath)
                val requestFile = file.asRequestBody("application/pdf".toMediaType())
                val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
                lifecycleScope.launch {
                    NotesApi.retrofitService.uploadPdf(body)
                    nrPdfCaricati = nrPdfCaricati + 1
                    findViewById<TextView>(R.id.tvNrPdf).text = "PDF caricati: $nrPdfCaricati"

                }
            } else {
                println("Failed to get file path from URI")
            }
        } else {
            println("No file selected")
        }

    }
    private fun uriToFilePath(context: Context, uri: Uri): String? {
        return try {
            // Query the ContentResolver for the file's display name
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = if (cursor?.moveToFirst() == true) {
                nameIndex?.let { cursor.getString(it) }
            } else {
                null
            }
            cursor?.close()

            // Use the file's display name to create a new File object
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream != null && fileName != null) {
                val file = File(context.cacheDir, fileName)
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                file.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    //implementazione back arrow button nell'app bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}