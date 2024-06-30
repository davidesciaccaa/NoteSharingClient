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
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.data.UserSession
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

class Login: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)      //prende il login.xml della cartella res/layout

        val editTextUsername = findViewById<EditText>(R.id.editT_username)
        val editTextPassword = findViewById<EditText>(R.id.editT_password)
        val btnLogin = findViewById<Button>(R.id.btn_login)
        val tvErroreLogin = findViewById<TextView>(R.id.tv_erroreLogin)
        val btnSignup = findViewById<Button>(R.id.btn_signup)
        val btnSignupGoogle = findViewById<Button>(R.id.btn_signupGoogle)

        btnLogin.setOnClickListener {
            //NotesApi.init(this)
            //Controllo login Admin (per modificare l'ip)
            var resultLogin: Response<MessageResponse>? = null
            lifecycleScope.launch {
                try {
                    resultLogin = NotesApi.retrofitService.uploadLogin(
                        UserSession(editTextUsername.text.toString(), editTextPassword.text.toString())
                    )
                    if (resultLogin != null && resultLogin?.isSuccessful == true) {
                        val responseBody = resultLogin!!.body()
                        if (responseBody != null && responseBody.message == "Login successful") {
                            saveLoginState(editTextUsername.text.toString())
                            redirectToMainActivity()
                        } else {
                            tvErroreLogin.text = "Credenziali errate."
                        }
                    } else {
                        tvErroreLogin.text = "Credenziali errate."
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

        btnSignup.setOnClickListener{
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

    }
    private fun saveLoginState(username: String) {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isLoggedIn", true)
        editor.putString("username", username)
        editor.apply()
    }
    private fun redirectToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}