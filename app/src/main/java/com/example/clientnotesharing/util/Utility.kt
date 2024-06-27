package com.example.clientnotesharing.util

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext

class Utility {

    fun getUsername(context: Context): String {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val username = sharedPreferences.getString("username", null)
        if(username != null) {
            return username
        } else return ""
    }

}