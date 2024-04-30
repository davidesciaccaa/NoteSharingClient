package com.example.clientnotesharing

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.data.NotesApi
import com.example.clientnotesharing.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
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
        setContent{
            showResult()
        }
         */
        var response:MaterialeFisico = MaterialeFisico()
        updateTextView("Prova")
        // Call the API method inside a coroutine scope. 'E una sorta di thread
        lifecycleScope.launch {
            try {
                response = NotesApi.retrofitService.getMaterialeFisico()
                Log.d("MainActivity", "*************************Response: $response")
                updateTextView(response.descrizioneMateriale)
                //findViewById<TextView>(R.id.tvProva).text = response.descrizioneMateriale

            } catch (e: HttpException) {
                // Exception thrown for an HTTP error response (e.g., 404, 500).
                // You can access the error response with e.response().
            } catch (e: IOException) {
                // Exception thrown for a network error (e.g., Timeout, No connectivity).
            } catch (e: Exception) {
                // Other unexpected exceptions.
            }
        }

    }

    private fun updateTextView(s: String) {
        // Make sure the response is not null and the TextView exists
        if (s != null) {
            findViewById<TextView>(R.id.tvProva)?.text = s
        }
    }

    /*
    fun getNotes() {
        val listResult = NotesApi.retrofitService.getMaterialeFisico()
    }
    //se uso jetpackCompose
    @Composable
    fun showResult(){

        val viewModel: NotesViewModel = viewModel()
        val result = viewModel.notesUiState
        BasicText(text = result.descrizioneMateriale)
    }

     */
}