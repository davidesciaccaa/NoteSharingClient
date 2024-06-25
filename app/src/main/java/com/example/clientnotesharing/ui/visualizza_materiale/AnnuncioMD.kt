package com.example.clientnotesharing.ui.visualizza_materiale

import android.app.Activity
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

class AnnuncioMD : AppCompatActivity() {

    private lateinit var createDocumentLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_md)

        createDocumentLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val fileName = result.data?.getStringExtra("pdfName") ?: "default_name.pdf"
                    val fileContent = result.data?.getByteArrayExtra("fileContent") ?: ByteArray(0)
                    writeBytesToDocument(uri, fileName, fileContent)
                }
            } else {
                Toast.makeText(this, "PDF creation canceled", Toast.LENGTH_SHORT).show()
            }
        }

        val jsonStringA = intent.getStringExtra("AnnuncioSelezionato")
        val jsonStringM = intent.getStringExtra("MaterialeAssociato")

        if (jsonStringA != null && jsonStringM != null) {
            val AnnuncioSelezionato = Json.decodeFromString<Annuncio>(jsonStringA)
            val MaterialeDigitaleAssociato = Json.decodeFromString<MaterialeDigitale>(jsonStringM)

            // appbar
            supportActionBar?.apply {
                title = AnnuncioSelezionato.titolo // cambio il titolo dell'app bar della view aperta
                setDisplayHomeAsUpEnabled(true)
                setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
            }

            val tvDataAn = findViewById<TextView>(R.id.tvDataAnnuncio)
            val vtDescrizioneAnnuncioMD = findViewById<TextView>(R.id.vtDescrizioneAnnuncioMD)
            val tvEmailProprietarioMD = findViewById<TextView>(R.id.tvEmailProprietarioMD)
            val tvAnnoRifMD = findViewById<TextView>(R.id.tvAnnoRifMD)
            val tvNomeCorsoMD = findViewById<TextView>(R.id.tvNomeCorsoMD)
            val tvDescrMaterialeD = findViewById<TextView>(R.id.tvDescrMaterialeD)
            val btnScaricaPDFs = findViewById<TextView>(R.id.btnDownloadPDFs)

            tvDataAn.text = AnnuncioSelezionato.data
            vtDescrizioneAnnuncioMD.text = AnnuncioSelezionato.descrizioneAnnuncio
            tvEmailProprietarioMD.text = "TO DO**************"
            tvAnnoRifMD.text = MaterialeDigitaleAssociato.annoRiferimento.toString()
            tvNomeCorsoMD.text = AnnuncioSelezionato.AreaToString()
            tvDescrMaterialeD.text = MaterialeDigitaleAssociato.descrizioneMateriale

            // pdf
            btnScaricaPDFs.setOnClickListener {
                lifecycleScope.launch {
                    val listaDatiDigitali = fetchDatoDigitale(AnnuncioSelezionato.id)
                    Log.e("AnnuncioMD", "àààààààààààààààààààà: ${listaDatiDigitali}")
                    if (listaDatiDigitali.isNotEmpty()) {
                        for (i in listaDatiDigitali) {
                            val bytePdf = i.fileBytes
                            val namepdf = i.fileName
                            createPdfDocument(bytePdf, namepdf)
                        }
                    } else {
                        // To do
                        Log.e("AnnuncioMD", "Errore: non è stato ricevuto nulla dal server")
                    }
                }
            }
        } else {
            Log.e("AnnuncioMD", "Intent extras AnnuncioSelezionato or MaterialeAssociato is null")
            Toast.makeText(this, "Intent data missing or corrupted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createPdfDocument(bytePDF: ByteArray, pdfName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/pdf"
            putExtra(Intent.EXTRA_TITLE, "$pdfName.pdf")
            putExtra("fileContent", bytePDF)
            putExtra("pdfName", pdfName)
        }
        createDocumentLauncher.launch(intent)
    }

    private fun writeBytesToDocument(uri: Uri, fileName: String, fileContent: ByteArray) {
        try {
            contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(fileContent)
                Toast.makeText(this, "PDF saved successfully", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error creating PDF", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private suspend fun fetchDatoDigitale(idAnnuncio: String): ArrayList<DatoDigitale> {
        return try {
            val response = NotesApi.retrofitService.getPDFs(idAnnuncio)
            if (response.isSuccessful) {
                response.body()?.let { listaDatiDigitali ->
                    Log.e("AnnuncioMD", "àààààààààààààààààààà: ${listaDatiDigitali[0].fileName}")
                    listaDatiDigitali
                } ?: ArrayList()
            } else {
                Log.e("AnnuncioMD", "The response from the server was not successful: ${response.message()}")
                ArrayList()
            }
        } catch (e: HttpException) {
            Log.e("AnnuncioMD", "HTTP Exception: ${e.message()}")
            e.printStackTrace()
            ArrayList()
        } catch (e: IOException) {
            Log.e("AnnuncioMD", "IO Exception: ${e.message}")
            e.printStackTrace()
            ArrayList()
        } catch (e: Exception) {
            Log.e("AnnuncioMD", "Exception: ${e.message}")
            e.printStackTrace()
            ArrayList()
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
