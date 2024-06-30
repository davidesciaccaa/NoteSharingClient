package com.example.clientnotesharing.ui.visualizza_materiale

import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import com.google.android.gms.maps.MapView
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlinx.serialization.json.Json


class AnnuncioMF: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_mf)

        val AnnuncioSelezionato = intent.getStringExtra("AnnuncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeFisicoAssociato = intent.getStringExtra("MaterialeAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        //appbar
        supportActionBar?.apply {
            title = AnnuncioSelezionato.titolo
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        val tvDataAnnuncio = findViewById<TextView>(R.id.tvData)
        val tvEmail = findViewById<TextView>(R.id.tvEmailProprietario)
        val tvCosto = findViewById<TextView>(R.id.tvCosto)
        val tvAnnoMateriale = findViewById<TextView>(R.id.tvAnnoRif)
        val tvDescrMateriale = findViewById<TextView>(R.id.tvDescMateriale)
        val tvCorso = findViewById<TextView>(R.id.tvCorso)
        val tvIndirizzo = findViewById<TextView>(R.id.tvIndirizzo)
        //val map = findViewById<MapView>(R.id.map_fragment)
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_fragment) as? MapFragment
//        val mapOptions = MapOptions(mapKey = "lRQwcMyFY7tTIhvMG8GWArsPPGCaZz6b")
//        val mapFragment = MapFragment.newInstance(mapOptions)
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.map_container, mapFragment)
//            .commit()
//        mapFragment.getMapAsync { tomtomMap: TomTomMap ->
//            // Your code goes here
//        }


        //this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta
        //Il modo corretto è scrivere il testo in strings, avendo dei placeholder che vengono passati qua:
        tvDataAnnuncio.text = AnnuncioSelezionato.data//LocalDate.now().toString() //data corrente
        tvEmail.text = getString(R.string.proprietarioEmail, AnnuncioSelezionato.idProprietario) //devo avere 1 metodo che mi recupera la mail di quetso utente ************************
        tvCosto.text = getString(R.string.costo, MaterialeFisicoAssociato.costo)
        tvAnnoMateriale.text = getString(R.string.anno_riferimento, MaterialeFisicoAssociato.annoRiferimento)
        tvDescrMateriale.text = MaterialeFisicoAssociato.descrizioneMateriale
        tvCorso.text = getString(R.string.corso_riferimento, AnnuncioSelezionato.AreaToString())
        tvIndirizzo.text = getString(R.string.indirizzo_ritiro, MaterialeFisicoAssociato.provincia, MaterialeFisicoAssociato.comune, MaterialeFisicoAssociato.via, MaterialeFisicoAssociato.numeroCivico)


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