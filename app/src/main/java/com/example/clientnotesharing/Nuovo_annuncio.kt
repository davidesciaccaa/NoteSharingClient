package com.example.clientnotesharing

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.util.UUID

class Nuovo_annuncio: AppCompatActivity() {
    private val usernameUtenteLoggato = "usernameUtenteLoggato" // Replace with your actual username

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
                }
            } else {
                println("Failed to get file path from URI")
            }
        } else {
            println("No file selected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_annuncio)

        findViewById<Button>(R.id.buttonSelezionaPDF).setOnClickListener {
            pickPdfFiles.launch("application/pdf") //per selezionare solo pdf
        }
        val editTextNomeAnnuncio = findViewById<EditText>(R.id.editTextNomeAnnuncio)
        val editTextMultiLineDescrizioneAnnuncio = findViewById<EditText>(R.id.editTextMultiLineDescrizioneAnnuncio)
        val switchMateriale = findViewById<Switch>(R.id.switchMateriale)
        val buttonSelezionaPDF = findViewById<Button>(R.id.buttonSelezionaPDF)
        val TextViewCountPDF = findViewById<TextView>(R.id.TextViewCountPDF)
        val editTextNumberAnnoMD = findViewById<EditText>(R.id.editTextNumberAnnoMD)
        val TextMultiLineDescrizioneMD = findViewById<EditText>(R.id.TextMultiLineDescrizioneMD)
        val editTextNomeCorsoMD = findViewById<EditText>(R.id.editTextNomeCorsoMD)
        val editTextNumberAnnoMF = findViewById<EditText>(R.id.editTextNumberAnnoMF)
        val MultiLineDescrizioneMF = findViewById<EditText>(R.id.MultiLineDescrizioneMF)
        val editTextNomeCorsoMF = findViewById<EditText>(R.id.editTextNomeCorsoMF)
        val editTextNumberDecimalCostoMF = findViewById<EditText>(R.id.editTextNumberDecimalCostoMF)
        val editTextNumberNumeroCivico = findViewById<EditText>(R.id.editTextNumberNumeroCivico)
        val editTextViaRitiro = findViewById<EditText>(R.id.editTextViaRitiro)
        val editTextProvinciaRitiro = findViewById<EditText>(R.id.editTextProvinciaRitiro)
        val editTextComuneRitiro = findViewById<EditText>(R.id.editTextComuneRitiro)
        val editTextNumberCAP = findViewById<EditText>(R.id.editTextNumberCAP)
        val buttonConferma = findViewById<Button>(R.id.buttonConferma)
        // TO DO: sistemare lo Switch

        MultiLineDescrizioneMF.isEnabled = false
        editTextNomeCorsoMF.isEnabled = false
        editTextNumberAnnoMF.isEnabled = false
        editTextNumberDecimalCostoMF.isEnabled = false
        editTextNumberNumeroCivico.isEnabled = false
        editTextViaRitiro.isEnabled = false
        editTextProvinciaRitiro.isEnabled = false
        editTextComuneRitiro.isEnabled = false
        editTextNumberCAP.isEnabled = false

        var materialeDigitale: MaterialeDigitale? = null
        var materialeFisico: MaterialeFisico? = null
        var statoSwitch = false
        switchMateriale.setOnCheckedChangeListener { buttonView, isChecked ->
            statoSwitch = true
            editTextNumberAnnoMD.isEnabled = !isChecked
            TextMultiLineDescrizioneMD.isEnabled = !isChecked
            editTextNomeCorsoMD.isEnabled = !isChecked
            buttonSelezionaPDF.isEnabled = !isChecked
            MultiLineDescrizioneMF.isEnabled = isChecked
            editTextNomeCorsoMF.isEnabled = isChecked
            editTextNumberAnnoMF.isEnabled = isChecked
            editTextNumberDecimalCostoMF.isEnabled = isChecked
            editTextNumberNumeroCivico.isEnabled = isChecked
            editTextViaRitiro.isEnabled = isChecked
            editTextProvinciaRitiro.isEnabled = isChecked
            editTextComuneRitiro.isEnabled = isChecked
            editTextNumberCAP.isEnabled = isChecked

        }


        buttonConferma.setOnClickListener{
            if(statoSwitch){ // gestire dopo i casi in cui sono vuoti quelli che prendono int altrimenti non funziona la conversione
                materialeFisico = MaterialeFisico(
                    editTextNumberDecimalCostoMF.text.toString().toInt(),
                    editTextNumberAnnoMF.text.toString().toInt(),
                    editTextNomeCorsoMF.text.toString(),
                    MultiLineDescrizioneMF.text.toString(),
                    editTextComuneRitiro.text.toString(),
                    editTextProvinciaRitiro.text.toString(),
                    editTextViaRitiro.text.toString(),
                    editTextNumberCAP.text.toString().toInt()
                )
            }else{
                materialeDigitale = MaterialeDigitale(
                    editTextNumberAnnoMD.text.toString().toInt(),
                    editTextNomeCorsoMD.text.toString(),
                    TextMultiLineDescrizioneMD.text.toString()
                )
            }

            val ID: UUID = UUID.randomUUID()
            val nuovoA = Annuncio(
                ID.toString(),
                editTextNomeAnnuncio.text.toString(),
                LocalDate.now().toString(),
                editTextMultiLineDescrizioneAnnuncio.text.toString(),
                usernameUtenteLoggato,
                materialeDigitale,
                materialeFisico
            )
            lifecycleScope.launch {
                NotesApi.retrofitService.uploadAnnuncio(nuovoA)
            }

            //to do: chiudere la pagina Nuovo annuncio
            //to do: controllo che non sono rimasti vuoti
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



}
