package com.example.clientnotesharing.util

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.sdk.location.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.Semaphore
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class Utility {

    fun getUsername(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        if(username != null) {
            return username
        } else return ""
    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    suspend fun getGeoPointFromAddress(context: Context, address: String): GeoPoint? {
        return withContext(Dispatchers.IO) {
            suspendCoroutine<GeoPoint?> { continuation ->
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

}