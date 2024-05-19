package com.example.clientnotesharing

import android.os.Bundle
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.serialization.json.Json

class AnnuncioMD: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_md)

        val AnnuncioSelezionato = intent.getStringExtra("AnunncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeFisicoAssociato = intent.getStringExtra("MaterialeAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        val tvDataAnnuncio = findViewById<TextView>(R.id.tvData)


        this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta

        tvDataAnnuncio.text = ""//data corrente

    }
}