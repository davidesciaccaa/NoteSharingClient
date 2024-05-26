package com.example.clientnotesharing

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class Nuovo_Materiale_Fisico: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_materiale_fisico)

        val editTextNumberAnnoMF = findViewById<EditText>(R.id.editNAnno)
        val multiLineDescrizioneMF = findViewById<EditText>(R.id.MultiLineDescr)
        val editTextNomeCorsoMF = findViewById<EditText>(R.id.editTNomeCorso)
        val editTextNumberDecimalCostoMF = findViewById<EditText>(R.id.editNDecimalCosto)
        val editTextNumberNumeroCivico = findViewById<EditText>(R.id.editNNumeroCivico)
        val editTextViaRitiro = findViewById<EditText>(R.id.editTViaR)
        val editTextProvinciaRitiro = findViewById<EditText>(R.id.editTProvincia)
        val editTextComuneRitiro = findViewById<EditText>(R.id.editTComuneRitiro)
        val editTextNumberCAP = findViewById<EditText>(R.id.editNCAP)
        val buttonConferma = findViewById<Button>(R.id.btnCreaNuovoA)
        val buttonIndietro = findViewById<Button>(R.id.btnIndietro)

        buttonConferma.setOnClickListener{
            val nuovoA = intent.getStringExtra("nuovoA").let {
                Json.decodeFromString<Annuncio>(it!!)
            }
            val nuovoMf = MaterialeFisico(
                nuovoA.id,
                editTextNumberDecimalCostoMF.text.toString().toInt(),
                editTextNumberAnnoMF.text.toString().toInt(),
                editTextNomeCorsoMF.text.toString(),
                multiLineDescrizioneMF.text.toString(),
                editTextComuneRitiro.text.toString(),
                editTextProvinciaRitiro.text.toString(),
                editTextViaRitiro.text.toString(),
                editTextNumberNumeroCivico.text.toString().toInt(),
                editTextNumberCAP.text.toString().toInt()
                )
            lifecycleScope.launch {
                NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                NotesApi.retrofitService.uploadMaterialeFisico(nuovoMf)
            }

            //to do: chiudere la pagina Nuovo annuncio
            //to do: controllo che non sono rimasti vuoti
        }
    }
}