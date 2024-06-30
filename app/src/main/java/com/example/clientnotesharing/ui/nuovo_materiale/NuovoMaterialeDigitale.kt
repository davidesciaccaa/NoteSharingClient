package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.MainActivity
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.DatoDigitale
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MessageResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.File
import java.util.UUID

class NuovoMaterialeDigitale: AppCompatActivity() {
    private var nrPdfCaricati = 0
    private var nuovoAid: String = ""
    private var datoD: ArrayList<DatoDigitale> = ArrayList<DatoDigitale>()

    private val pickPdfFiles = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle the returned Uri
        if (uri != null) {
            val filePath = uriToFilePath(this, uri)
            if (filePath != null) {
                val file = File(filePath)
                val datoDigitale = DatoDigitale(UUID.randomUUID().toString(), nuovoAid, file.readBytes(), file.name)
                Log.d("TAG", "**** dato digitale: ${datoDigitale.fileName} byte: ${datoDigitale.fileBytes}")
                datoD.add(datoDigitale)
                val textViewNrPdf = findViewById<TextView>(R.id.tvNrPdf)
                nrPdfCaricati++
                textViewNrPdf.text = getString(R.string.PDFSelezionati, nrPdfCaricati)
            } else {
                Log.d("TAG", "Failed to get file path from URI")
            }
        } else {
            Log.d("TAG", "No file selected")
        }
    }

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

        val nuovoA = intent.getStringExtra("nuovoA").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        nuovoAid = nuovoA.id

        val btnSelezionaPDF = findViewById<Button>(R.id.btnSelezionaPDF)
        val editTAnno = findViewById<EditText>(R.id.editTextNumberAnno)
        val editMultilineDescr = findViewById<EditText>(R.id.editTextTextMultiLineDescrizione)
        val buttonConferma = findViewById<Button>(R.id.btnCreaNuovoA)
        val buttonIndietro = findViewById<Button>(R.id.btnIndietro)
        val textViewNrPdf = findViewById<TextView>(R.id.tvNrPdf)
        val tvError = findViewById<TextView>(R.id.tvErroreMD)
        val tvAttesaPdf = findViewById<TextView>(R.id.tvAttesaPdf)

        textViewNrPdf.text = getString(R.string.PDFSelezionati, nrPdfCaricati)
        btnSelezionaPDF.setOnClickListener {
            pickPdfFiles.launch("application/pdf")
        }
        buttonConferma.setOnClickListener{
            tvAttesaPdf.text = resources.getString(R.string.attendi_caricamento)
            if (
                editTAnno.text.toString().isNotEmpty() &&
                editMultilineDescr.text.toString().isNotEmpty() &&
                nrPdfCaricati > 0 &&
                editTAnno.text.toString().length == 4
            ) {
                val nuovoMD = MaterialeDigitale(
                    nuovoA.id,
                    editTAnno.text.toString().toInt(),
                    editMultilineDescr.text.toString()
                )
                lifecycleScope.launch(Dispatchers.IO) {
                    Log.d("TAG", "Coroutine started")
                    try {
                        if (datoD.isNotEmpty()) {
                            var responseAnnuncio =NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                            var responseMaterialeDigitale =NotesApi.retrofitService.uploadMaterialeDigitale(nuovoMD)
                            var responseListPDF: ArrayList<Response<MessageResponse>> = ArrayList()
                            for(dato in datoD){
                                responseListPDF.add(NotesApi.retrofitService.uploadPdf(dato))
                            }

                            if (!(responseAnnuncio.isSuccessful && responseMaterialeDigitale.isSuccessful)) {
                                var check = false
                                for(response in responseListPDF) {
                                    if(!response.isSuccessful){
                                        check = true
                                    }
                                }
                                if (check) {
                                    Toast.makeText(this@NuovoMaterialeDigitale, "Failed to retrieve PDF content", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("TAG", "Coroutine exception: ${e.message}", e)
                    } finally {
                        Log.d("TAG", "Coroutine completed")
                    }
                    closeActivities()
                }
            }else{
                tvError.text = resources.getString(R.string.completta_tutti_campi)
            }
        }
        buttonIndietro.setOnClickListener{
            onBackPressedDispatcher.onBackPressed() //clicca il back button
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
    private fun closeActivities() {
        // Start the new activity with flags to clear the back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
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
