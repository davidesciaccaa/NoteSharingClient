package com.example.clientnotesharing.ui.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.CambioPasswordRequest
import com.example.clientnotesharing.dbLocale.DbHelper
import com.example.clientnotesharing.ui.sign_up_login.Login
import com.example.clientnotesharing.util.Utility
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        findViewById<TextView>(R.id.tvHelloUser).text = getString(R.string.hello_user,  Utility().getUsername(this@SettingsActivity))
        findViewById<TextView>(R.id.btnLogOut).setOnClickListener{
            logout()
        }
        findViewById<TextView>(R.id.btnCambioPsw).setOnClickListener{
            val oldPassword = findViewById<TextView>(R.id.editTextOldPsw).text.toString()
            val newPassword = findViewById<TextView>(R.id.editTextNewPsw).text.toString()
            if(oldPassword.isNotBlank() && newPassword.isNotBlank()){
                lifecycleScope.launch {
                    try {
                        val result = NotesApi.retrofitService.cambioPsw(CambioPasswordRequest(oldPassword, newPassword, Utility().getUsername(this@SettingsActivity)))
                        if(result.isSuccessful) {
                            logout()
                        } else {
                            findViewById<TextView>(R.id.tvErrore).text = getString(R.string.psw_not_changed)
                        }
                    } catch (e: HttpException) {
                        Log.e("LoginActivity", "HTTP Exception: ${e.message}")
                        e.printStackTrace()
                    } catch (e: IOException) {
                        Log.e("LoginActivity", "IO Exception: ${e.message}")
                        e.printStackTrace()
                    } catch (e: Exception) {
                        Log.e("LoginActivity", "Exception: ${e.message}")
                        e.printStackTrace()
                    }
                }
            }
        }
    }
    private fun logout() {
        //elimino db locale
        val dbHelper = DbHelper(this@SettingsActivity)
        dbHelper.deleteDatabase()
        // Elimino lo username dalle SharedPreferences
        val sharedPreferences = this.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()  // Clears all data in SharedPreferences
        editor.apply()
        // apro la loginscreen
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK //faccio clear del backstack
        startActivity(intent)
    }
}