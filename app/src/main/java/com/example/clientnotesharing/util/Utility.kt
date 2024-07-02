package com.example.clientnotesharing.util

import android.content.Context
import android.location.Geocoder
import android.location.Geocoder.GeocodeListener
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.tomtom.sdk.location.GeoPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale

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
            val geocoder = Geocoder(context, Locale.ITALY)

            //************
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