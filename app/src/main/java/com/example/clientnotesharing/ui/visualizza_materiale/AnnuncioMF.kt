package com.example.clientnotesharing.ui.visualizza_materiale

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

/*
 * Classe per la View che mostra i dati di un annuncio con dati fisici
 */
class AnnuncioMF: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_mf)
        // Riceve i dati dalle classi che chiamano la visualizzazione (Home, Personali, Preferiti o anche dai marker nella mappa)
        val AnnuncioSelezionato = intent.getStringExtra("AnnuncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeFisicoAssociato = intent.getStringExtra("MaterialeAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        // appbar
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
        val btnApriMappa = findViewById<Button>(R.id.btnApriMappa)

        tvDataAnnuncio.text = AnnuncioSelezionato.data
        // Riceve la email del proprietario dell'annuncio
        lifecycleScope.launch {
            val response =
                NotesApi.retrofitService.getMailFromUsername(AnnuncioSelezionato.idProprietario)
            if (response.isSuccessful) {
                val email = response.body()?.message
                if (email != null) {
                    tvEmail.text = getString(R.string.proprietario_email_valore, email)
                } else {
                    Log.d(
                        "ComandiAnnunciListView, fun clickMateriale",
                        "ComandiAnnunciListView, fun clickMateriale: Error: Response from server ${response.message()}"
                    )
                }
            }
        }
        tvCosto.text = getString(R.string.costo_valore, MaterialeFisicoAssociato.costo)
        tvAnnoMateriale.text = getString(R.string.anno_riferimento_valore, MaterialeFisicoAssociato.annoRiferimento)
        tvDescrMateriale.text = MaterialeFisicoAssociato.descrizioneMateriale
        tvCorso.text = getString(R.string.corso_riferimento_valore, AnnuncioSelezionato.AreaToString())
        tvIndirizzo.text = getString(R.string.indirizzo_ritiro_valori, MaterialeFisicoAssociato.provincia, MaterialeFisicoAssociato.comune, MaterialeFisicoAssociato.via, MaterialeFisicoAssociato.numeroCivico)

        // Trasformo l'indirizzo in coordinate ed apro la dinestra per la visualizzazione nella mappa
        val indirizzoMappa = "${MaterialeFisicoAssociato.comune}, via ${MaterialeFisicoAssociato.via} ${MaterialeFisicoAssociato.numeroCivico}"
        btnApriMappa.setOnClickListener{
            val intent = Intent(this@AnnuncioMF, MappaAnnuncio::class.java)
            val indirizzoSerializzato = Json.encodeToString(String.serializer(), indirizzoMappa)
            intent.putExtra("indirizzo", indirizzoSerializzato)
            startActivity(intent)
        }

    }
    // Implementazione back arrow button nell'app bar
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