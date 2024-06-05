package com.example.clientnotesharing

import android.app.SearchManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.databinding.ActivityMainBinding
import com.example.clientnotesharing.dbLocale.dbHelper
import com.example.clientnotesharing.ui.nuovo_materiale.Nuovo_annuncio
import com.example.clientnotesharing.ui.signUpLogin.Login
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMD
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMF
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        if (!isLoggedIn()) {
            redirectToLogin()
            return
        }
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

        //bottone per creare nuovi annunci
        findViewById<FloatingActionButton>(R.id.addbtn).setOnClickListener{
            val intent = Intent(this, Nuovo_annuncio::class.java)
            startActivity(intent)
        }

    }
    private fun isLoggedIn(): Boolean {
        val sharedPreferences = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", false)
    }
    private fun redirectToLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish() //così non rimane nel backstack
    }

    /*
    //deve essere messo in HomeFragment visto che lì è la listView????? 'E la ricerca
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        /*
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        val component = ComponentName(this, MainActivity::class.java)
        val searchableInfo = searchManager.getSearchableInfo(component)
        searchView.setSearchableInfo(searchableInfo)
        return true
        */


        val manager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu?.findItem(R.id.action_search)
        val searchView = searchItem?.actionView as SearchView
        searchView.setSearchableInfo(manager.getSearchableInfo(componentName))

        var listaAdapter = findViewById<ListView>(R.id.listViewAnnunci).adapter as MyAdapter


        //val database = dbHelper(this)
        //var listaAnnunci = database.getAllData()

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchView.clearFocus()
                searchView.setQuery("", false)
                //searchView.collapseActionView()
                searchView.setIconified(true)
                //listaAdapter.filter.filter(query)
                Toast.makeText(this@MainActivity, "Looking for $query", Toast.LENGTH_LONG).show()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Implement what you want to do when the query text changes
                var results:ArrayList<Annuncio> = ArrayList<Annuncio>()
                if (!newText.isNullOrEmpty()) {
                    /*
                    for (elem in listaAnnunci) {
                        if (elem.titolo.contains(newText, ignoreCase = true)) {
                            results.add(elem)
                        }
                    }

                    val data = ArrayList<HashMap<String, Any>>()
                    if (results.isNotEmpty()) {
                        for (elem in results) {
                            val hm = HashMap<String, Any>()
                            hm["Tittle"] = elem.titolo
                            hm["Date"] = elem.data
                            data.add(hm)
                        }
                    }
                    listaAdapter = SimpleAdapter(
                        this@MainActivity, //nelle classi normali mettiamo this
                        data,
                        R.layout.listlayout,
                        arrayOf("Tittle", "Date"),
                        intArrayOf(R.id.textViewTittle, R.id.textViewData)  //si chiamano cosi quelli di simple_list_item_2
                    )

                     */
                    listaAdapter.filter.filter(newText)
                }

                return false
            }
        })


        return super.onCreateOptionsMenu(menu)


    }

     */



}