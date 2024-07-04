package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.MainActivity
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.util.Utility
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/*
 * Classe per la View della creazione di un mateeriale (di un annuncio) di tipo fisico
 */
class NuovoMaterialeFisico: AppCompatActivity() {
    private lateinit var editTextNumberAnnoMF: EditText
    private lateinit var multiLineDescrizioneMF: EditText
    private lateinit var editTextNumberDecimalCostoMF: EditText
    private lateinit var editTextNumberNumeroCivico: EditText
    private lateinit var editTextViaRitiro: EditText
    private lateinit var editTextProvinciaRitiro: EditText
    private lateinit var editTextComuneRitiro: EditText
    private lateinit var editTextNumberCAP: EditText
    private lateinit var buttonConferma: Button
    private lateinit var buttonIndietro: Button
    private lateinit var tvError: TextView
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_materiale_fisico)

        editTextNumberAnnoMF = findViewById(R.id.editNAnno)
        multiLineDescrizioneMF = findViewById(R.id.MultiLineDescr)
        editTextNumberDecimalCostoMF = findViewById(R.id.editNDecimalCosto)
        editTextNumberNumeroCivico = findViewById(R.id.editNNumeroCivico)
        editTextViaRitiro = findViewById(R.id.editTViaR)
        editTextProvinciaRitiro = findViewById(R.id.editTProvincia)
        editTextComuneRitiro = findViewById(R.id.editTComuneRitiro)
        editTextNumberCAP = findViewById(R.id.editNCAP)
        buttonConferma = findViewById(R.id.btnCreaNuovoA)
        buttonIndietro = findViewById(R.id.btnIndietro)
        tvError = findViewById(R.id.tvErroreMF)

        // Aggiunta backArrow button nell'app bar
        supportActionBar?.apply {
            title = getString(R.string.nuovo_materiale_fisico)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        Utility().gestioneLandscape(window, resources)

        buttonConferma.setOnClickListener{
            if (controlli()) {
                // Ricevo l'annuncio inviato dalla classe NuovoAnnuncio
                val nuovoA = intent.getStringExtra("nuovoA").let {
                    Json.decodeFromString<Annuncio>(it!!)
                }
                val nuovoMf = MaterialeFisico(
                    nuovoA.id,
                    editTextNumberDecimalCostoMF.text.toString().toInt(),
                    editTextNumberAnnoMF.text.toString().toInt(),
                    multiLineDescrizioneMF.text.toString(),
                    editTextComuneRitiro.text.toString(),
                    editTextProvinciaRitiro.text.toString(),
                    editTextViaRitiro.text.toString(),
                    editTextNumberNumeroCivico.text.toString().toInt(),
                    editTextNumberCAP.text.toString().toInt()
                    )
                lifecycleScope.launch {
                    //invio al server l'annuncio e il materiale
                    var responseAnnuncio = NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                    var responseMaterialeFisico = NotesApi.retrofitService.uploadMaterialeFisico(nuovoMf)
                    // Controllo le risposte del server
                    if (!(responseAnnuncio.isSuccessful && responseMaterialeFisico.isSuccessful)) {
                        Toast.makeText(this@NuovoMaterialeFisico, "Failed to retrieve PDF content", Toast.LENGTH_SHORT).show()
                    }
                }
                closeActivities() //chiude la view e va in Home
            }else{
                tvError.text = resources.getString(R.string.completta_tutti_campi)
            }
        }
        buttonIndietro.setOnClickListener{
            onBackPressedDispatcher.onBackPressed() //clicca il back button
        }
    }
    // Metodo che apre la home screen e "pulisce" il backstack
    private fun closeActivities() {
        // Start the new activity with flags to clear the back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
    // Per il funzionamento del back button nell'app bar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed() //clicca il back button
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    // Controlli sui campi da complettare prima di proseguire
    private fun controlli(): Boolean {
        return editTextNumberAnnoMF.text.toString().isNotBlank() &&
                multiLineDescrizioneMF.text.toString().isNotBlank() &&
                editTextNumberDecimalCostoMF.text.toString().isNotBlank() &&
                editTextNumberNumeroCivico.text.toString().isNotBlank() &&
                editTextViaRitiro.text.toString().isNotBlank() &&
                editTextProvinciaRitiro.text.toString().isNotBlank() &&
                editTextComuneRitiro.text.toString().isNotBlank() &&
                editTextNumberCAP.text.toString().isNotBlank() &&
                editTextNumberAnnoMF.text.toString().length == 4 &&
                editTextNumberCAP.text.toString().length == 5
    }

}