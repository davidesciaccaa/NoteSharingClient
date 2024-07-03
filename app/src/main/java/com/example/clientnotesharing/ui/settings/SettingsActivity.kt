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

/*
 * Classe per la View delle Impostazioni
 */
class SettingsActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings)

        findViewById<TextView>(R.id.tvHelloUser).text = getString(R.string.ciao_valore,  Utility().getUsername(this@SettingsActivity))
        // btn di logOut
        findViewById<TextView>(R.id.btnLogOut).setOnClickListener{
            logout()
        }
        // btn di cambio password
        findViewById<TextView>(R.id.btnCambioPsw).setOnClickListener{
            val oldPassword = findViewById<TextView>(R.id.editTextOldPsw).text.toString()
            val newPassword = findViewById<TextView>(R.id.editTextNewPsw).text.toString()
            if(oldPassword.isNotBlank() && newPassword.isNotBlank()){ // controlli
                lifecycleScope.launch {
                    try {
                        // Invio al server la ricchiesta
                        val result = NotesApi.retrofitService.cambioPsw(CambioPasswordRequest(oldPassword, newPassword, Utility().getUsername(this@SettingsActivity)))
                        if(result.isSuccessful) { // controllo la risposta del server
                            logout()
                        } else {
                            findViewById<TextView>(R.id.tvErrore).text = getString(R.string.psw_non_cambiata)
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
    // Metodo che effettua il logOut
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