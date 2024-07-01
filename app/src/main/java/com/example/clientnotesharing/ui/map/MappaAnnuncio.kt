package com.example.clientnotesharing.ui.map

import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
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
import com.tomtom.sdk.location.OnLocationUpdateListener
import kotlinx.serialization.json.Json

class MappaAnnuncio: AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mappa)

        val indirizzoRicevuto = intent.getStringExtra("indirizzo").let {
            Json.decodeFromString<String>(it!!)
        }

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
                    val geoPoint = Utility().getGeoPointFromAddress(this@MappaAnnuncio, indirizzoRicevuto) //Dobbiamo passare l'indirizzo dell'annuncio
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

//                // gesture della mappa
//                tomtomMap.addMapPanningListener(
//                    object : MapPanningListener {
//                        override fun onMapPanningEnded() {
//                            // YOUR CODE GOES HERE
//                        }
//
//                        override fun onMapPanningOngoing() {
//                            // YOUR CODE GOES HERE
//                        }
//
//                        override fun onMapPanningStarted() {
//                            // YOUR CODE GOES HERE
//                        }
//                    },
//                )

            }
        }
    }
}