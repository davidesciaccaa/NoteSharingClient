package com.example.clientnotesharing.ui.visualizza_materiale

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import okhttp3.MultipartBody
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

        //appbar
        supportActionBar?.apply {
            title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        val tvDataAn = findViewById<TextView>(R.id.tvDataAnnuncio)
        val vtDescrizioneAnnuncioMD = findViewById<TextView>(R.id.vtDescrizioneAnnuncioMD)
        val tvEmailProprietarioMD = findViewById<TextView>(R.id.tvEmailProprietarioMD)
        val tvAnnoRifMD = findViewById<TextView>(R.id.tvAnnoRifMD)
        val tvNomeCorsoMD = findViewById<TextView>(R.id.tvNomeCorsoMD)
        val tvDescrMaterialeD = findViewById<TextView>(R.id.tvDescrMaterialeD)
        val btnScaricaPDFs = findViewById<TextView>(R.id.btnDownloadPDFs)

        //this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta

        tvDataAn.text = AnnuncioSelezionato.data
        vtDescrizioneAnnuncioMD.text = AnnuncioSelezionato.descrizioneAnnuncio
        tvEmailProprietarioMD.text = "TO DO**************"
        tvAnnoRifMD.text = MaterialeDigitaleAssociato.annoRiferimento.toString()
        tvNomeCorsoMD.text = MaterialeDigitaleAssociato.areaMateriale.toString()
        tvDescrMaterialeD.text = MaterialeDigitaleAssociato.descrizioneMateriale

        var multipartPDF: MultipartBody.Part? = null
        btnScaricaPDFs.setOnClickListener{
            lifecycleScope.launch {
                try {
                    var response = NotesApi.retrofitService.getPDFs(AnnuncioSelezionato.id)
                    if(response.isSuccessful){
                        multipartPDF = response.body()
                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
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
                if(multipartPDF != null){
                    multipartPDF?.let{ part ->
                        //GESTISCI LE PARTI
                        val headers = part.headers
                        val contentDisposition = headers?.get("Content-Disposition")
                        //val fileName = contentDisposition?.let { parseFileName(it) }

                        val filename = contentDisposition?.substringAfter("filename=\"")?.substringBefore("\"")

                        //val contenutoFile = part.body.bytes()
                    }

                }

            }
        }

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