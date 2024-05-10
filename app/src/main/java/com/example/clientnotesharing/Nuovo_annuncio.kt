package com.example.clientnotesharing

import android.os.Bundle
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
        // Handle the returned Uri
        if (uri != null) {
            val file = File(uri.path) // convert Uri to File
            val requestFile = file.asRequestBody("application/pdf".toMediaType())
            val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
            lifecycleScope.launch {
                NotesApi.retrofitService.uploadPdf(body)

            }
        } else {
            println("No file selected")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_annuncio)

        findViewById<Button>(R.id.buttonSelectPDF).setOnClickListener {
            pickPdfFiles.launch("application/pdf")
        }

    }

}
