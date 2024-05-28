package com.example.clientnotesharing.ui.nuovo_materiale

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class Nuovo_Materiale_Fisico: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_materiale_fisico)

        supportActionBar?.apply {
            title = getString(R.string.titolo_appbar_nuovo_materiale_fisico)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

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
                //invio al server
                NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                NotesApi.retrofitService.uploadMaterialeFisico(nuovoMf)
                //forse conviene avere exceptions per gestirle qua??
            }
            finish() //chiude la view
            //to do: controllo che non sono rimasti vuoti ***************
            //***********************************
            //************************************
        }
        buttonIndietro.setOnClickListener{
            onBackPressedDispatcher.onBackPressed() //clicca il back button
        }
    }

    //implementazione back arrow button nell'app bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() //clicca il back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}