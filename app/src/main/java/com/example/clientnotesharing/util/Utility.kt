package com.example.clientnotesharing.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import android.os.HandlerThread
import android.util.Log
import android.view.Window
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.GeoPoint
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.Semaphore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.time.Duration.Companion.milliseconds

/*
 * Classe utility che offre funzionalità comuni
 */
class Utility {
    // Metodo che restituisce lo username dell'utente che sta usando l'app
    fun getUsername(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        if(username != null) {
            return username
        } else return ""
    }

    // Metodo che trasforma un indirizzo in coordinate geografiche. Nota è neccesaria almeno la versione 33 del SDK
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getGeoPointFromAddress(context: Context, address: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine { continuation ->
                val geocoder = Geocoder(context, Locale.ITALY)
                geocoder.getFromLocationName(address, 1, object : Geocoder.GeocodeListener {
                    override fun onGeocode(addresses: MutableList<Address>) {
                        val geopoint = if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val latitude = address.latitude
                            val longitude = address.longitude
                            GeoPoint(latitude, longitude)
                        } else {
                            null
                        }
                        continuation.resume(geopoint)
                    }

                    override fun onError(errorMessage: String?) {
                        continuation.resume(null) // Handle error case
                    }
                })
            }
        }
    }
    // Aggiunta di Location Provider per mostrare la posizione attuale
    @SuppressLint("MissingPermission") // perchè il controllo viene fatto all'inizio
    fun setupLocationProvider(tomtomMap: TomTomMap, context: Context, ) {
        val androidLocationProviderConfig =
            AndroidLocationProviderConfig(
                minTimeInterval = 250L.milliseconds,
                minDistance = Distance.meters(20.0),
            )
        val locationHandlerThread = HandlerThread("locationHandlerThread")
        locationHandlerThread.start()
        val androidLocationProvider: LocationProvider =
            AndroidLocationProvider(
                context = context,
                locationLooper = locationHandlerThread.looper,
                config = androidLocationProviderConfig,
            )
        // location attuale
        tomtomMap.setLocationProvider(androidLocationProvider)
        androidLocationProvider.enable()
        val locationMarkerOptions =
            LocationMarkerOptions(
                type = LocationMarkerOptions.Type.Pointer,
            )
        tomtomMap.enableLocationMarker(locationMarkerOptions)

        val onLocationUpdateListener =
            OnLocationUpdateListener { location: GeoLocation ->
            }
        androidLocationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
        androidLocationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)
    }

    @RequiresApi(Build.VERSION_CODES.R)
    fun gestioneLandscape(window: Window, resources: Resources) {
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {// Metto in fullscreen
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            if (controller != null) {
                controller.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }

            // Per non interferire con i cutout delle camere
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val layoutParams = window.attributes
                layoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER
                window.attributes = layoutParams
            }
        }
    }


}