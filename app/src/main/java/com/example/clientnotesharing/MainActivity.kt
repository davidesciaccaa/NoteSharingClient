package com.example.clientnotesharing

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clientnotesharing.databinding.ActivityMainBinding
import com.example.clientnotesharing.ui.nuovo_materiale.Nuovo_annuncio
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        /*
        TODO controllo login
        if (!isLoggedIn()) {
            goToLoginActivity()
        }
        */

        //bottone +
        findViewById<FloatingActionButton>(R.id.addbtn).setOnClickListener{
            val intent = Intent(this, Nuovo_annuncio::class.java)
            startActivity(intent)
        }


        // Call the API method inside a coroutine scope. 'E una sorta di thread
        lifecycleScope.launch {
            try {
                //val response = NotesApi.retrofitService.getMaterialeFisico()
                //Log.d("MainActivity", "*************************Response: $response")
                //findViewById<TextView>(R.id.tvProva).text = response.descrizioneMateriale

            } catch (e: HttpException) {
                Log.e("MainActivity", "HTTP Exception: ${e.message()}")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("MainActivity", "IO Exception: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }

    }



}