package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.util.Utility
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.UUID

/*
 * Classe per la View della creazione di un nuovo annuncio
 */
class NuovoAnnuncio : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    // Variabili per i spinner
    private var itemSelez = ""
    private var areaSelez = ""

    // metodo per l'item selezionato negli spinner
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.id == R.id.spinner) {
            itemSelez = parent.getItemAtPosition(position).toString()
        } else if (parent?.id == R.id.spinnerArea) {
            areaSelez = parent.getItemAtPosition(position).toString()
        }
        findViewById<Button>(R.id.btnAvanti).isEnabled = itemSelez.isNotEmpty() && areaSelez.isNotEmpty()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Non gestiamo questo caso
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_annuncio)

        // Aggiunta backArrow button nell'app bar
        supportActionBar?.apply {
            title = getString(R.string.nuovo_annuncio)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)


        }

        Utility().gestioneLandscape(window, resources)

        val editTextNomeAnnuncio = findViewById<EditText>(R.id.editTextNomeAnnuncio)
        val buttonConferma = findViewById<Button>(R.id.btnAvanti)
        val buttonCancella = findViewById<Button>(R.id.btnCancella)
        val tvError = findViewById<TextView>(R.id.tvErrore)
        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)
        val spinnerMateriale = findViewById<Spinner>(R.id.spinner)
        buttonConferma.isEnabled = false

        // Spinner
        setupSpinnerMateriale(spinnerMateriale)
        setupSpinnerArea(spinnerArea)

        buttonConferma.setOnClickListener {
            if (editTextNomeAnnuncio.text.toString().isNotBlank()) {
                // Se tutti i campi non sono vuoti
                val ID: UUID = UUID.randomUUID()
                //creo il nuovo annuncio
                val nuovoA = Annuncio(
                    ID.toString(),
                    editTextNomeAnnuncio.text.toString(),
                    LocalDate.now().toString(),
                    if (itemSelez == "Materiale Fisico") true else false,
                    Utility().getUsername(this@NuovoAnnuncio),
                    spinnerArea.selectedItemPosition
                )
                //apro la corrispondente finestra
                val intent = if (itemSelez == "Materiale Fisico") {
                    Intent(this, NuovoMaterialeFisico::class.java)
                } else {
                    Intent(this, NuovoMaterialeDigitale::class.java)
                }
                val jsonString = Json.encodeToString(Annuncio.serializer(), nuovoA)
                intent.putExtra("nuovoA", jsonString)
                startActivity(intent)
                tvError.text = "" // cosi se si torna indietro il testo di errore non c'è più
            } else {
                // Ci sono ancora campi da compilare
                tvError.text = resources.getString(R.string.completta_tutti_campi)
            }
        }
        buttonCancella.setOnClickListener {
            finish()
        }
    }

    // Metodo per il setup dello spiner che permette all'utente di selezionare un'area
    private fun setupSpinnerArea(spinnerArea: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.area_spinner_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerArea.adapter = adapter
            // Set default selection
            spinnerArea.setSelection(0)
            areaSelez = adapter.getItem(0).toString()
        }
        spinnerArea.onItemSelectedListener = this
    }

    // Metodo per il setup dello spiner che permette all'utente di selezionare il tipo dell'annuncio
    private fun setupSpinnerMateriale(spinnerMateriale: Spinner) {
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerMateriale.adapter = adapter
            // Set default selection
            spinnerMateriale.setSelection(0)
            itemSelez = adapter.getItem(0).toString()
        }
        spinnerMateriale.onItemSelectedListener = this
    }

    // Per il funzionamento del back button nell'app bar
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
