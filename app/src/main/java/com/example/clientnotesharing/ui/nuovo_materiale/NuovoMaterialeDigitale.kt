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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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

/*
 * Classe per la View della creazione di un materiale (di un annuncio) di tipo digitale
 */
class NuovoMaterialeDigitale: AppCompatActivity() {
    private var nrPdfCaricati = 0
    private var nuovoAid: String = ""
    private var datoD: ArrayList<DatoDigitale> = ArrayList<DatoDigitale>()

    // res
    private lateinit var btnSelezionaPDF: Button
    private lateinit var editTAnno: EditText
    private lateinit var editMultilineDescr: EditText
    private lateinit var buttonConferma: Button
    private lateinit var buttonIndietro: Button
    private lateinit var textViewNrPdf: TextView
    private lateinit var tvError: TextView
    private lateinit var tvAttesaPdf: TextView

    private val pickPdfFiles = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val filePath = uriToFilePath(this, uri)
            if (filePath != null) {
                val file = File(filePath)
                val datoDigitale = DatoDigitale(UUID.randomUUID().toString(), nuovoAid, file.readBytes(), file.name)
                datoD.add(datoDigitale)
                val textViewNrPdf = findViewById<TextView>(R.id.tvNrPdf)
                nrPdfCaricati++
                textViewNrPdf.text = getString(R.string.pdf_selezionati_valore, nrPdfCaricati)
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

        // Aggiungo il back btn all'app bar
        supportActionBar?.apply {
            title = getString(R.string.nuovo_materiale_digitale)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        btnSelezionaPDF = findViewById(R.id.btnSelezionaPDF)
        editTAnno = findViewById(R.id.editTextNumberAnno)
        editMultilineDescr = findViewById(R.id.editTextTextMultiLineDescrizione)
        buttonConferma = findViewById(R.id.btnCreaNuovoA)
        buttonIndietro = findViewById(R.id.btnIndietro)
        textViewNrPdf = findViewById(R.id.tvNrPdf)
        tvError = findViewById(R.id.tvErroreMD)
        tvAttesaPdf = findViewById(R.id.tvAttesaPdf)

        // Ricevo l'annuncio inviato dalla classe NuovoAnnuncio
        val nuovoA = intent.getStringExtra("nuovoA").let {
            Json.decodeFromString<Annuncio>(it!!)
        }

        nuovoAid = nuovoA.id // var di classe. Serve per creare l'oggetto DatoDigitale

        textViewNrPdf.text = getString(R.string.pdf_selezionati_valore, nrPdfCaricati)
        btnSelezionaPDF.setOnClickListener {
            pickPdfFiles.launch("application/pdf") // permetto solo la selezione dei pdf
        }
        buttonConferma.setOnClickListener{
            tvAttesaPdf.text = resources.getString(R.string.attendi_caricamento)
            if (controlli()) {
                val nuovoMD = MaterialeDigitale(
                    nuovoA.id,
                    editTAnno.text.toString().toInt(),
                    editMultilineDescr.text.toString()
                )
                lifecycleScope.launch{
                    try {
                        inviaAlServer(nuovoA, nuovoMD) // Invio tutto al server
                    } catch (e: Exception) {
                        Log.e("TAG", "Coroutine exception: ${e.message}", e)
                    } finally {
                        Log.e("TAG", "Coroutine completed")
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
    // Invio al server tutto e controllo i risultati
    private suspend fun inviaAlServer(nuovoA: Annuncio, nuovoMD: MaterialeDigitale) {
        if (datoD.isNotEmpty()) { // datoD è una lista che contiene i pdf selezionati dall'utente
            var responseAnnuncio =NotesApi.retrofitService.uploadAnnuncio(nuovoA) // invio l'annuncio al server
            Log.e("TAG", "*********: ${nuovoA.id}")
            Log.e("TAG", "*********: ${nuovoMD.id}")
            var responseMaterialeDigitale =NotesApi.retrofitService.uploadMaterialeDigitale(nuovoMD) // invio il materiale al server
            var responseListPDF: ArrayList<Response<MessageResponse>> = ArrayList()
            for(dato in datoD){
                Log.e("TAG", "pdf********${dato.idAnnuncio}")
                // invio tutti i pdf selezionati
                responseListPDF.add(NotesApi.retrofitService.uploadPdf(dato))
            }
            // Controllo le risposte del server
            if (!(responseAnnuncio.isSuccessful && responseMaterialeDigitale.isSuccessful)) {
                var check = false
                for(response in responseListPDF) {
                    // controllo le risposte di ogni pdf
                    if(!response.isSuccessful){
                        check = true
                    }
                }
                if (check) {
                    Toast.makeText(this@NuovoMaterialeDigitale, "Failed to retrieve PDF content", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // I controlli di tutti i campi prima di continuare
    private fun controlli(): Boolean {
        return editTAnno.text.toString().isNotBlank() &&
                editMultilineDescr.text.toString().isNotBlank() &&
                nrPdfCaricati > 0 &&
                editTAnno.text.toString().length == 4
    }
    // Metodo per ottenere il file path a partire dall'uri del file
    private fun uriToFilePath(context: Context, uri: Uri): String? {
        return try {
            // Query per ottenere il nome del file
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val fileName = if (cursor?.moveToFirst() == true) {
                nameIndex?.let { cursor.getString(it) }
            } else {
                null
            }
            cursor?.close()

            // Creo un nuovo file
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
    // Metodo che apre la home screen e "pulisce" il backstack
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
