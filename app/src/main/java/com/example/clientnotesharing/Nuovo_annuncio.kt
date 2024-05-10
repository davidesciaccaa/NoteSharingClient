package com.example.clientnotesharing

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class Nuovo_annuncio: AppCompatActivity() {
    private val pickPdfFiles = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        // Handle the returned Uri
        if (uri != null) {
            // Do something with the Uri
            println("Selected file: $uri")
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
