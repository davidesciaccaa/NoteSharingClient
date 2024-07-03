package com.example.clientnotesharing.ui.sign_up_login

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
import com.example.clientnotesharing.data.Persona
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/*
 * Classe per la View della registrazione di un nuovo utente
 */
class SignUp: AppCompatActivity() {
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextCodiceFiscale: EditText
    private lateinit var editTextNome: EditText
    private lateinit var editTextCognome: EditText
    private lateinit var editTextProvincia: EditText
    private lateinit var editTextComune: EditText
    private lateinit var editTextVia: EditText
    private lateinit var editTextNumeroCivico: EditText
    private lateinit var editTextCAP: EditText
    private lateinit var editTextDataDiNascita: EditText
    private lateinit var tvErroreSignUp: TextView
    private lateinit var btnConferma: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)     //prende il signup.xml della cartella res/layout

        editTextUsername = findViewById(R.id.editT_username_signup)
        editTextEmail = findViewById(R.id.editT_email_signup)
        editTextPassword = findViewById(R.id.editT_password_signup)
        editTextCodiceFiscale = findViewById(R.id.editT_codice_fiscale)
        editTextNome = findViewById(R.id.editT_nome)
        editTextCognome = findViewById(R.id.editT_cognome)
        editTextProvincia = findViewById(R.id.editT_provincia)
        editTextComune = findViewById(R.id.editT_comune)
        editTextVia = findViewById(R.id.editT_via)
        editTextNumeroCivico = findViewById(R.id.editT_numero_civico)
        editTextCAP = findViewById(R.id.editT_cap)
        editTextDataDiNascita = findViewById(R.id.editT_data_di_nascita)
        tvErroreSignUp = findViewById(R.id.tv_errore_sign_up)
        btnConferma = findViewById(R.id.btn_conferma_registrazione)

        btnConferma.setOnClickListener {
            if (controlliCampi()) {
                Log.e("controlloPsw", "Pcfcf ")
                lifecycleScope.launch {
                    try {
                        val resultSignUp = NotesApi.retrofitService.uploadSignUp(
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
                        if (resultSignUp != null && resultSignUp.isSuccessful) {
                            val responseBody = resultSignUp.body()
                            if (responseBody != null && responseBody.message == "User registered successfully") {
                                val intent = Intent(this@SignUp, MainActivity::class.java)
                                startActivity(intent)
                            } else {
                                tvErroreSignUp.text = "Errore"
                            }
                        } else {
                            tvErroreSignUp.text = "Errore"
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
            } else tvErroreSignUp.text = "Errore. Controlla tutti i dati"
        }

    }
    // Controllo su tutti i campi
    private fun controlliCampi(): Boolean {
        return  editTextUsername.text.toString().isNotBlank() &&
                editTextEmail.text.toString().isNotBlank() &&
                editTextPassword.text.toString().isNotBlank() &&
                editTextCodiceFiscale.text.toString().isNotBlank() &&
                editTextNome.text.toString().isNotBlank() &&
                editTextCognome.text.toString().isNotBlank() &&
                editTextProvincia.text.toString().isNotBlank() &&
                editTextComune.text.toString().isNotBlank() &&
                editTextVia.text.toString().isNotBlank() &&
                editTextNumeroCivico.text.toString().isNotBlank() &&
                editTextCAP.text.toString().length == 5 &&
                //editTextDataDiNascita.text.toString() &&
                controlloPsw(editTextPassword.text.toString()) &&
                editTextCodiceFiscale.text.toString().length == 16
    }
    // Controlli sulla password
    private fun controlloPsw(password: String): Boolean {
        val minLength = 8
        var hasUpperCase = false
        var hasLowerCase = false
        var hasDigit = false

        if (password.length < minLength) return false

        for (c in password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpperCase = true
            if (Character.isLowerCase(c)) hasLowerCase = true
            if (Character.isDigit(c)) hasDigit = true
        }

        Log.e("controlloPsw", "Password has upper case: $hasUpperCase")
        Log.e("controlloPsw", "Password has lower case: $hasLowerCase")
        Log.e("controlloPsw", "Password has digit: $hasDigit")
        return hasUpperCase && hasLowerCase && hasDigit
    }

}