package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Intent
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
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
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.intellij.lang.annotations.Language
import java.util.Locale

class NuovoMaterialeFisico: AppCompatActivity() {
    //per lo spinner
    private var itemSelez = ""
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
        val editTextNumberDecimalCostoMF = findViewById<EditText>(R.id.editNDecimalCosto)
        val editTextNumberNumeroCivico = findViewById<EditText>(R.id.editNNumeroCivico)
        val editTextViaRitiro = findViewById<EditText>(R.id.editTViaR)
        val editTextProvinciaRitiro = findViewById<EditText>(R.id.editTProvincia)
        val editTextComuneRitiro = findViewById<EditText>(R.id.editTComuneRitiro)
        val editTextNumberCAP = findViewById<EditText>(R.id.editNCAP)
        val buttonConferma = findViewById<Button>(R.id.btnCreaNuovoA)
        val buttonIndietro = findViewById<Button>(R.id.btnIndietro)
        val tvError = findViewById<TextView>(R.id.tvErroreMF)

        buttonConferma.setOnClickListener{
            if (
                editTextNumberAnnoMF.text.toString().isNotBlank() &&
                multiLineDescrizioneMF.text.toString().isNotBlank() &&
                editTextNumberDecimalCostoMF.text.toString().isNotBlank() &&
                editTextNumberNumeroCivico.text.toString().isNotBlank() &&
                editTextViaRitiro.text.toString().isNotBlank() &&
                editTextProvinciaRitiro.text.toString().isNotBlank() &&
                editTextComuneRitiro.text.toString().isNotBlank() &&
                editTextNumberCAP.text.toString().isNotBlank() &&
                editTextNumberAnnoMF.text.toString().length == 4 &&
                editTextNumberCAP.text.toString().length == 5
                //&& indirizzoInCoordinate()
            ) {
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
                    //invio al server
                    var responseAnnuncio = NotesApi.retrofitService.uploadAnnuncio(nuovoA)
                    var responseMaterialeFisico = NotesApi.retrofitService.uploadMaterialeFisico(nuovoMf)
                    if (!(responseAnnuncio.isSuccessful && responseMaterialeFisico.isSuccessful)) {
                        Toast.makeText(this@NuovoMaterialeFisico, "Failed to retrieve PDF content", Toast.LENGTH_SHORT).show()
                    }
                    //forse conviene avere exceptions per gestirle qua??
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

//    @RequiresApi(Build.VERSION_CODES.TIRAMISU) //almeno android 13
//    private fun indirizzoInCoordinate(nrCivico: Int, via: String, ): Boolean {
//        Geocoder(this@NuovoMaterialeFisico, Locale.ITALY).getFromLocationName("Varese, viale Aguggiari 169",1, Geocoder.GeocodeListener {  })
//        return false
//    }

    private fun closeActivities() {
        // Start the new activity with flags to clear the back stack
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}