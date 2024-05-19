package com.example.clientnotesharing

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException


class AnnuncioMF: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_mf)

        val AnnuncioSelezionato = intent.getStringExtra("AnunncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeFisicoAssociato = intent.getStringExtra("MaterialeFisicoAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        val tvDataAnnuncio = findViewById<TextView>(R.id.tvData)
        val tvDescrAnnuncio = findViewById<TextView>(R.id.tvDescriizioneAnnuncio)
        val tvEmail = findViewById<TextView>(R.id.tvEmailProprietario)
        val tvCosto = findViewById<TextView>(R.id.tvCosto)
        val tvAnnoMateriale = findViewById<TextView>(R.id.tvAnnoRif)
        val tvDescrMateriale = findViewById<TextView>(R.id.tvDescMateriale)
        val tvCorso = findViewById<TextView>(R.id.tvCorso)
        val tvIndirizzo = findViewById<TextView>(R.id.tvIndirizzo)

        this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta

        tvDataAnnuncio.text = ""//data corrente
        tvDescrAnnuncio.text = AnnuncioSelezionato.descrizioneAnnuncio
        tvEmail.text = AnnuncioSelezionato.idProprietario //devo avere 1 metodo che mi recupera la mail di quetso utente
        tvCosto.text = MaterialeFisicoAssociato.costo.toString()
        tvAnnoMateriale.text = MaterialeFisicoAssociato.annoRiferimento.toString()
        tvDescrMateriale.text = MaterialeFisicoAssociato.descrizioneMateriale
        tvCorso.text = MaterialeFisicoAssociato.nomeCorso
        tvIndirizzo.text = MaterialeFisicoAssociato.provincia + " " + MaterialeFisicoAssociato.comune + " " + MaterialeFisicoAssociato.via  + " " + MaterialeFisicoAssociato.numeroCivico.toString()

    }
}