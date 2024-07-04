package com.example.clientnotesharing.ui.visualizza_materiale

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.R
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.example.clientnotesharing.util.Utility
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.map.display.style.StyleMode
import kotlinx.serialization.json.Json

/*
 * Classe per la isualizzazione della posizione di un annuncio (fisico) in una View che contiene solo una mappa
 */
class MappaAnnuncio: AppCompatActivity() {
    // Variabile per la location permission
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mappa)

        // Aggiunta backArrow button nell'app bar
        supportActionBar?.apply {
            title = getString(R.string.mappa)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        Utility().gestioneLandscape(window, resources)

        val indirizzoRicevuto = intent.getStringExtra("indirizzo").let {
            Json.decodeFromString<String>(it!!)
        }

        // Controllo dei permessi di location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            viewInMap(indirizzoRicevuto)
        }

    }
    // Mappa
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun viewInMap(indirizzoRicevuto: String) {
        val myMap = supportFragmentManager.findFragmentById(R.id.map_fragment) as? MapFragment
        if (myMap != null) {
            myMap.getMapAsync { tomtomMap: TomTomMap ->
                // Stile mappa
                val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    tomtomMap.setStyleMode(StyleMode.DARK)
                } else {
                    tomtomMap.setStyleMode(StyleMode.MAIN)
                }
                Utility().setupLocationProvider(tomtomMap, applicationContext)
                addMarkerToMap(tomtomMap, indirizzoRicevuto)
            }
        }
    }
    // Aggiunta di marker nella mappa
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun addMarkerToMap(tomtomMap: TomTomMap, indirizzoRicevuto: String) {
        lifecycleScope.launch {
            val geoPoint = Utility().getGeoPointFromAddress(this@MappaAnnuncio, indirizzoRicevuto) //Dobbiamo passare l'indirizzo dell'annuncio
            if (geoPoint != null) {
                val markerOptions =
                    MarkerOptions(
                        coordinate = geoPoint,
                        pinImage = ImageFactory.fromResource(R.drawable.map_pin),
                    )
                tomtomMap.addMarker(markerOptions)
            } else {
                Log.e("GeoPoint", "Address not found")
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