package com.example.clientnotesharing.ui.signUpLogin

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.clientnotesharing.R

class SignUp: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signup)     //prende il signup.xml della cartella res/layout


    }
}