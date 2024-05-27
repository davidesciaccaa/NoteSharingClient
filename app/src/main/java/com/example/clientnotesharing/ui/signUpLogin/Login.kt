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
            var resultLogin: Response<MessageResponse>? = null
            lifecycleScope.launch {
                try {
                    resultLogin = NotesApi.retrofitService.uploadLogin(
                        UserSession(editTextUsername.text.toString(), editTextPassword.text.toString())
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
                if (resultLogin != null && resultLogin!!.isSuccessful) {
                    val responseBody = resultLogin!!.body()
                    if (responseBody != null && responseBody.message == "Login successful") {
                        val intent = Intent(this@Login, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        tvErroreLogin.text = "Credenziali errate."
                    }
                } else {
                    tvErroreLogin.text = "Credenziali errate."
                }
            }
        }


    }

}