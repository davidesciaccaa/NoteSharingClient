package com.example.clientnotesharing.ui.nuovo_materiale

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.UUID

class NuovoAnnuncio : AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private var itemSelez = ""
    private var areaSelez = ""

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent?.id == R.id.spinner) {
            itemSelez = parent.getItemAtPosition(position).toString()
        } else if (parent?.id == R.id.spinnerArea) {
            areaSelez = parent.getItemAtPosition(position).toString()
        }

        // Check if both selections are made
        checkSelections()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Handle case where nothing is selected, if needed
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_annuncio)

        supportActionBar?.apply {
            title = getString(R.string.titolo_appbar_nuovo_annuncio)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        val editTextNomeAnnuncio = findViewById<EditText>(R.id.editTextNomeAnnuncio)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val spinnerArea = findViewById<Spinner>(R.id.spinnerArea)
        val buttonConferma = findViewById<Button>(R.id.btnAvanti)
        val buttonCancella = findViewById<Button>(R.id.btnCancella)
        val tvError = findViewById<TextView>(R.id.tvErrore)
        buttonConferma.isEnabled = false

        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
            // Set default selection
            spinner.setSelection(0)
            itemSelez = adapter.getItem(0).toString()
        }
        spinner.onItemSelectedListener = this

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

        buttonConferma.setOnClickListener {
            if (editTextNomeAnnuncio.text.toString().isNotEmpty()) {
                val ID: UUID = UUID.randomUUID()
                if (itemSelez == "Materiale Fisico") {
                    val nuovoA = Annuncio(
                        ID.toString(),
                        editTextNomeAnnuncio.text.toString(),
                        LocalDate.now().toString(),
                        true, //è un materiale fisico
                        getUsername(),
                        spinnerArea.selectedItemPosition,
                        false
                    )

                    val intent = Intent(this, NuovoMaterialeFisico::class.java)
                    val jsonString = Json.encodeToString(Annuncio.serializer(), nuovoA)
                    intent.putExtra("nuovoA", jsonString)
                    startActivity(intent)
                    tvError.text = "" // cosi se si torna indietro il testo di errore non c'è più
                } else if (itemSelez == "Materiale Digitale") {
                    val nuovoA = Annuncio(
                        ID.toString(),
                        editTextNomeAnnuncio.text.toString(),
                        LocalDate.now().toString(),
                        false, //è un materiale digitale
                        getUsername(),
                        spinnerArea.selectedItemPosition,
                        false
                    )

                    val intent = Intent(this, NuovoMaterialeDigitale::class.java)
                    val jsonString = Json.encodeToString(Annuncio.serializer(), nuovoA)
                    intent.putExtra("nuovoA", jsonString)
                    startActivity(intent)
                    tvError.text = "" // cosi se si torna indietro il testo di errore non c'è più
                }
            } else {
                tvError.text = resources.getString(R.string.completta_tutti_campi)
            }
        }
        buttonCancella.setOnClickListener {
            finish()
        }
    }

    private fun checkSelections() {
        val buttonConferma = findViewById<Button>(R.id.btnAvanti)
        buttonConferma.isEnabled = itemSelez.isNotEmpty() && areaSelez.isNotEmpty()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getUsername(): String {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getString("username", "") ?: ""
    }
}
