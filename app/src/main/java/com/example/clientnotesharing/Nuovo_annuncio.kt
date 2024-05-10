package com.example.clientnotesharing

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.OpenableColumns
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class Nuovo_annuncio: AppCompatActivity() {
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

        findViewById<Button>(R.id.buttonSelectPDF).setOnClickListener {
            pickPdfFiles.launch("application/pdf") //per selezionare solo pdf
        }

    }

    private fun uriToFilePath(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.cacheDir, "temp_file")
            if (inputStream != null) {
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
