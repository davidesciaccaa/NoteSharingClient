package com.example.clientnotesharing.ui.visualizza_materiale

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.DatoDigitale
import com.example.clientnotesharing.data.MaterialeDigitale
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException
import java.io.OutputStream

class AnnuncioMD : AppCompatActivity() {

    private lateinit var createDocumentLauncher: ActivityResultLauncher<Intent>
    private lateinit var arrayBytesFile: ByteArray

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_md)

        createDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    // Retrieve the byte array from intent extras
                    //val fileContent = result.data?.getByteArrayExtra("fileContent")
                    if (::arrayBytesFile.isInitialized) {
                        // Call your method to write bytes to document
                        writeBytesToDocument(uri, arrayBytesFile)
                    } else {
                        Toast.makeText(this, "Failed to retrieve PDF content", Toast.LENGTH_SHORT).show()

                        /*

                        IL PROBLEMA è QUA: non riesce  prendere i dati dal put extra perchè nel metodo createPdfDocument i byte ci sono
                         */
                    }
                }
            } else {
                Toast.makeText(this, "PDF creation canceled", Toast.LENGTH_SHORT).show()
            }
        }
        handleIntentExtras()

    }

    private fun handleIntentExtras() {
        val jsonStringA = intent.getStringExtra("AnnuncioSelezionato")
        val jsonStringM = intent.getStringExtra("MaterialeAssociato")

        if (jsonStringA != null && jsonStringM != null) {
            val annuncioSelezionato = Json.decodeFromString<Annuncio>(jsonStringA)
            val materialeDigitaleAssociato = Json.decodeFromString<MaterialeDigitale>(jsonStringM)

            setupAppBar(annuncioSelezionato.titolo)
            populateUI(annuncioSelezionato, materialeDigitaleAssociato)
        } else {
            Log.e("AnnuncioMD", "Intent extras AnnuncioSelezionato or MaterialeAssociato is null")
            Toast.makeText(this, "Intent data missing or corrupted\"", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setupAppBar(titoloAnnuncioSelezionato: String) {
        // appbar
        supportActionBar?.apply {
            title = titoloAnnuncioSelezionato // cambio il titolo dell'app bar della view aperta
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }
    }
    private fun populateUI(annuncioSelezionato: Annuncio, materialeDigitaleAssociato: MaterialeDigitale) {
        findViewById<TextView>(R.id.tvDataAnnuncio).text = annuncioSelezionato.data
        findViewById<TextView>(R.id.vtDescrizioneAnnuncioMD).text = annuncioSelezionato.descrizioneAnnuncio
        findViewById<TextView>(R.id.tvEmailProprietarioMD).text = "TO DO**************"
        findViewById<TextView>(R.id.tvAnnoRifMD).text = materialeDigitaleAssociato.annoRiferimento.toString()
        findViewById<TextView>(R.id.tvNomeCorsoMD).text = annuncioSelezionato.AreaToString()
        findViewById<TextView>(R.id.tvDescrMaterialeD).text = materialeDigitaleAssociato.descrizioneMateriale

        findViewById<TextView>(R.id.btnDownloadPDFs).setOnClickListener {
            lifecycleScope.launch {
                downloadPDFs(annuncioSelezionato.id)
            }
        }
    }
    private suspend fun downloadPDFs(annuncioId: String) {
        val listaDatiDigitali = fetchDatoDigitale(annuncioId)
        if (listaDatiDigitali.isNotEmpty()) {
            for (datoDigitale in listaDatiDigitali) {
                arrayBytesFile = datoDigitale.fileBytes
                createPdfDocument(datoDigitale.fileName)
            }
        } else {
            Log.e("AnnuncioMD", "Errore: non è stato ricevuto nulla dal server")
        }
    }

    //Funzione che crea un pdf vuoto
    private fun createPdfDocument(pdfName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, pdfName) // il titolo
            //putExtra("fileContent", bytePDF) //Il putExtra non funziona
        }

        createDocumentLauncher.launch(intent)
    }

    fun writeBytesToDocument(uri: Uri, content: ByteArray) {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        try {
            val outputStream: OutputStream? = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                outputStream.write(content)
                outputStream.close()
                Toast.makeText(applicationContext, "Document saved successfully", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(applicationContext, "Failed to open output stream", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(applicationContext, "Error writing to document: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun fetchDatoDigitale(idAnnuncio: String): ArrayList<DatoDigitale> {
        return try {
            val response = NotesApi.retrofitService.getPDFs(idAnnuncio)
            if (response.isSuccessful) {
                response.body()?.let { listaDatiDigitali ->
                    listaDatiDigitali
                } ?: ArrayList()
            } else {
                Log.e("AnnuncioMD", "The response from the server was not successful: ${response.message()}")
                ArrayList()
            }
        } catch (e: Exception) {
            Log.e("AnnuncioMD", "Exception: ${e.message}")
            e.printStackTrace()
            ArrayList() //restituisce una lista vuota in questo caso
        }
    }

    // implementazione back arrow button nell'app bar
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
