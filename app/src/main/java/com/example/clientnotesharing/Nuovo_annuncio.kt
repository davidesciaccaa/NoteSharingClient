package com.example.clientnotesharing

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.time.LocalDate
import java.util.UUID

class Nuovo_annuncio: AppCompatActivity(), AdapterView.OnItemSelectedListener {
    private val usernameUtenteLoggato = "ilibr"
    private var itemSelez = ""

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        // An item is selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos).
        itemSelez = parent?.getItemAtPosition(position).toString()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Another interface callback.
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.nuovo_annuncio)

        val editTextNomeAnnuncio = findViewById<EditText>(R.id.editTextNomeAnnuncio)
        val editTextMultiLineDescrizioneAnnuncio = findViewById<EditText>(R.id.editTextMultiLineDescrizioneAnnuncio)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val buttonConferma = findViewById<Button>(R.id.btnAvanti)

        // Per lo spinner
        ArrayAdapter.createFromResource(
            this,
            R.array.spinner_options,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            spinner.adapter = adapter
        }
        spinner.onItemSelectedListener = this

        buttonConferma.setOnClickListener{
            val ID: UUID = UUID.randomUUID()

            if (itemSelez.equals("Materiale Fisico")) {
                val nuovoA = Annuncio(
                    ID.toString(),
                    editTextNomeAnnuncio.text.toString(),
                    LocalDate.now().toString(),
                    editTextMultiLineDescrizioneAnnuncio.text.toString(),
                    true, //è un materiale fisico
                    usernameUtenteLoggato
                )

                val intent = Intent(this, Nuovo_Materiale_Fisico::class.java)
                val jsonString = Json.encodeToString(Annuncio.serializer(), nuovoA)
                intent.putExtra("nuovoA", jsonString)
                startActivity(intent)
            }else{
                if (itemSelez.equals("Materiale Digitale")) {
                    val nuovoA = Annuncio(
                        ID.toString(),
                        editTextNomeAnnuncio.text.toString(),
                        LocalDate.now().toString(),
                        editTextMultiLineDescrizioneAnnuncio.text.toString(),
                        false, //è un materiale digitale
                        usernameUtenteLoggato
                    )

                    val intent = Intent(this, Nuovo_Materiale_Digitale::class.java)
                    val jsonString = Json.encodeToString(Annuncio.serializer(), nuovoA)
                    intent.putExtra("nuovoA", jsonString)
                    startActivity(intent)
                }
            }
        }
    }





}
