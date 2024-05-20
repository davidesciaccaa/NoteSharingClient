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

class AnnuncioMD: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_md)

        val AnnuncioSelezionato = intent.getStringExtra("AnunncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeDigitaleAssociato = intent.getStringExtra("MaterialeAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        val tvDataAn = findViewById<TextView>(R.id.tvDataAnnuncio)
        val vtDescrizioneAnnuncioMD = findViewById<TextView>(R.id.vtDescrizioneAnnuncioMD)
        val tvEmailProprietarioMD = findViewById<TextView>(R.id.tvEmailProprietarioMD)
        val tvAnnoRifMD = findViewById<TextView>(R.id.tvAnnoRifMD)
        val tvNomeCorsoMD = findViewById<TextView>(R.id.tvNomeCorsoMD)
        val tvDescrMaterialeD = findViewById<TextView>(R.id.tvDescrMaterialeD)
        val btnScaricaPDFs = findViewById<TextView>(R.id.btnDownloadPDFs)

        this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta

        tvDataAn.text = AnnuncioSelezionato.data
        vtDescrizioneAnnuncioMD.text = AnnuncioSelezionato.descrizioneAnnuncio
        tvEmailProprietarioMD.text = "TO DO**************"
        tvAnnoRifMD.text = MaterialeDigitaleAssociato.annoRiferimento.toString()
        tvNomeCorsoMD.text = MaterialeDigitaleAssociato.nomeCorso
        tvDescrMaterialeD.text = MaterialeDigitaleAssociato.descrizioneMateriale

        btnScaricaPDFs.setOnClickListener{
            lifecycleScope.launch {
                try {
                    NotesApi.retrofitService.getPDFs(AnnuncioSelezionato.id)
                } catch (e: HttpException) {
                    Log.e("MainActivity", "HTTP Exception: ${e.message()}")
                    e.printStackTrace()
                } catch (e: IOException) {
                    Log.e("MainActivity", "IO Exception: ${e.message}")
                    e.printStackTrace()
                } catch (e: Exception) {
                    Log.e("MainActivity", "Exception: ${e.message}")
                    e.printStackTrace()
                }
            }.invokeOnCompletion {
                //TO DO: FARE QUALCOSA UNA VOLTA SCARICATI
            }
        }

    }
}