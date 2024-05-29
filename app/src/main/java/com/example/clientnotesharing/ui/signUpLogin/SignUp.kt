package com.example.clientnotesharing.ui.signUpLogin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.MainActivity
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.data.Persona
import com.example.clientnotesharing.data.UserSession
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class SignUp: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)     //prende il signup.xml della cartella res/layout

        val editTextUsername = findViewById<EditText>(R.id.editT_username_signup)
        val editTextEmail = findViewById<EditText>(R.id.editT_email_signup)
        val editTextPassword = findViewById<EditText>(R.id.editT_password_signup)
        val editTextCodiceFiscale = findViewById<EditText>(R.id.editT_codice_fiscale)
        val editTextNome = findViewById<EditText>(R.id.editT_nome)
        val editTextCognome = findViewById<EditText>(R.id.editT_cognome)
        val editTextProvincia = findViewById<EditText>(R.id.editT_provincia)
        val editTextComune = findViewById<EditText>(R.id.editT_comune)
        val editTextVia = findViewById<EditText>(R.id.editT_via)
        val editTextNumeroCivico = findViewById<EditText>(R.id.editT_numero_civico)
        val editTextCAP = findViewById<EditText>(R.id.editT_cap)
        val editTextDataDiNascita = findViewById<EditText>(R.id.editT_data_di_nascita)
        val tvErroreSignUp = findViewById<TextView>(R.id.tv_errore_sign_up)
        val btnConferma = findViewById<Button>(R.id.btn_conferma_registrazione)

        btnConferma.setOnClickListener {
            var resultSignUp: Response<MessageResponse>? = null
            lifecycleScope.launch {
                try {
                    resultSignUp = NotesApi.retrofitService.uploadSignUp(
                        Persona(
                            editTextUsername.text.toString(),
                            editTextEmail.text.toString(),
                            editTextPassword.text.toString(),
                            editTextCodiceFiscale.text.toString(),
                            editTextNome.text.toString(),
                            editTextCognome.text.toString(),
                            editTextProvincia.text.toString(),
                            editTextComune.text.toString(),
                            editTextVia.text.toString(),
                            editTextNumeroCivico.text.toString().toInt(),
                            editTextCAP.text.toString().toInt(),
                            editTextDataDiNascita.text.toString()
                        )
                    )
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
            }.invokeOnCompletion {
                if (resultSignUp != null && resultSignUp!!.isSuccessful) {
                    val responseBody = resultSignUp!!.body()
                    if (responseBody != null && responseBody.message == "User registered successfully") {
                        val intent = Intent(this@SignUp, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        tvErroreSignUp.text = "Errore."
                    }
                } else {
                    tvErroreSignUp.text = "Errore."
                }
            }
        }

    }

}