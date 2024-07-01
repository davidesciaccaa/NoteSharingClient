package com.example.clientnotesharing.ui.visualizza_materiale

import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeFisico
import com.google.android.gms.maps.MapView
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.map.display.MapOptions
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.location.road.SpeedLimit
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds


class AnnuncioMF: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.annuncio_mf)

        val AnnuncioSelezionato = intent.getStringExtra("AnnuncioSelezionato").let {
            Json.decodeFromString<Annuncio>(it!!)
        }
        val MaterialeFisicoAssociato = intent.getStringExtra("MaterialeAssociato").let {
            Json.decodeFromString<MaterialeFisico>(it!!)
        }

        //appbar
        supportActionBar?.apply {
            title = AnnuncioSelezionato.titolo
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.arrow_back_20dp)
        }

        val tvDataAnnuncio = findViewById<TextView>(R.id.tvData)
        val tvEmail = findViewById<TextView>(R.id.tvEmailProprietario)
        val tvCosto = findViewById<TextView>(R.id.tvCosto)
        val tvAnnoMateriale = findViewById<TextView>(R.id.tvAnnoRif)
        val tvDescrMateriale = findViewById<TextView>(R.id.tvDescMateriale)
        val tvCorso = findViewById<TextView>(R.id.tvCorso)
        val tvIndirizzo = findViewById<TextView>(R.id.tvIndirizzo)
        val Mymap = supportFragmentManager.findFragmentById(R.id.map_fragment) as? MapFragment


        Log.e("HomeFragment", "Inizio...")
        if (Mymap != null) {
            Log.e("HomeFragment", "dentro if...")
            Mymap.getMapAsync { tomtomMap: TomTomMap ->
                //Location provider
                val androidLocationProviderConfig =
                    AndroidLocationProviderConfig(
                        minTimeInterval = 250L.milliseconds,
                        minDistance = Distance.meters(20.0),
                    )
                val locationHandlerThread = HandlerThread("locationHandlerThread")
                locationHandlerThread.start()
                val androidLocationProvider: LocationProvider =
                    AndroidLocationProvider(
                        context = applicationContext,
                        locationLooper = locationHandlerThread.looper,
                        config = androidLocationProviderConfig,
                    )
                // location attuale
                tomtomMap.setLocationProvider(androidLocationProvider)
                androidLocationProvider.enable()
//                val mapLocationProvider = tomtomMap.getLocationProvider()
//                val isLocationInVisibleArea = tomtomMap.isCurrentLocationInMapBoundingBox
//                val currentLocation: GeoLocation? = tomtomMap.currentLocation

                val locationMarkerOptions =
                    LocationMarkerOptions(
                        type = LocationMarkerOptions.Type.Pointer,
                    )
                tomtomMap.enableLocationMarker(locationMarkerOptions)

                val onLocationUpdateListener =
                    OnLocationUpdateListener { location: GeoLocation ->
                    /* YOUR CODE GOES HERE */

                    }
                androidLocationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
                androidLocationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)

                //Pin di una posizione
                //val geoPoint = getGeoPointFromAddress("Milano, viale Ungheria, 46")
                //val geoPoint = GeoPoint(45.81719052411793, 8.82978810142905)
                // Inside your activity or fragment function
                lifecycleScope.launch {
                    val geoPoint = getGeoPointFromAddress("Varese, viale Aguggiari 169")
                    // Use geoPoint here, it will be null if no address was found
                    Log.e("HomeFragment", "coordinate $geoPoint")
                    if (geoPoint != null) {
                        val markerOptions =
                            MarkerOptions(
                                coordinate = geoPoint,
                                pinImage = ImageFactory.fromResource(R.drawable.map_pin),
                            )
                        tomtomMap.addMarker(markerOptions)
                        Log.e("HomeFragment", "Fine")
                    } else {
                        // Handle the case where no addresses were found
                        Log.d("GeoPoint", "Address not found")
                    }
                }


            }
        }


        //


        //this.title = AnnuncioSelezionato.titolo //cambio il titolo dell'app bar della view aperta
        //Il modo corretto è scrivere il testo in strings, avendo dei placeholder che vengono passati qua:
        tvDataAnnuncio.text = AnnuncioSelezionato.data//LocalDate.now().toString() //data corrente
        tvEmail.text = getString(R.string.proprietarioEmail, AnnuncioSelezionato.idProprietario) //devo avere 1 metodo che mi recupera la mail di quetso utente ************************
        tvCosto.text = getString(R.string.costo, MaterialeFisicoAssociato.costo)
        tvAnnoMateriale.text = getString(R.string.anno_riferimento, MaterialeFisicoAssociato.annoRiferimento)
        tvDescrMateriale.text = MaterialeFisicoAssociato.descrizioneMateriale
        tvCorso.text = getString(R.string.corso_riferimento, AnnuncioSelezionato.AreaToString())
        tvIndirizzo.text = getString(R.string.indirizzo_ritiro, MaterialeFisicoAssociato.provincia, MaterialeFisicoAssociato.comune, MaterialeFisicoAssociato.via, MaterialeFisicoAssociato.numeroCivico)


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
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private suspend fun getGeoPointFromAddress(address: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            val geocoder = Geocoder(this@AnnuncioMF, Locale.ITALY)
            val addresses = geocoder.getFromLocationName(address, 1)

            if (addresses?.isNotEmpty() == true) {
                val address = addresses.get(0)
                val latitude = address.latitude
                val longitude = address.longitude
                GeoPoint(latitude, longitude)
            } else {
                null
            }
        }
    }


}