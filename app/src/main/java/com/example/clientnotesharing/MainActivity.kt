package com.example.clientnotesharing

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clientnotesharing.databinding.ActivityMainBinding
import com.example.clientnotesharing.ui.nuovo_materiale.NuovoAnnuncio
import com.example.clientnotesharing.ui.sign_up_login.Login
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

// MainActivity, cioè il punto di partenza
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    //variabile per la gestione dei permessi della location
    private val LOCATION_PERMISSION_REQUEST_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        // Controllo se l'utente è stato loggato
        if (!isLoggedIn()) {
            redirectToLogin()
            return
        }
        setContentView(binding.root)

        // permessi di location
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_favorites, R.id.navigation_annunci_personali,R.id.navigation_visualizza_in_mappa
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // floating action btn per la creazione di nuovi annunci
        val fab = findViewById<FloatingActionButton>(R.id.addbtn)
        fab.setOnClickListener{
            val intent = Intent(this, NuovoAnnuncio::class.java)
            startActivity(intent)
        }
        // visibile solo in Home
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_home -> fab.show()
                else -> fab.hide()
            }
        }


    }

    // Metodo che controlla se l'utente ha già fatto il login in precedenza
    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE) // controlla nelle shared preferences
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }

    // Apre la schermata di login
    private fun redirectToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() // così non rimane nel backstack
    }
}