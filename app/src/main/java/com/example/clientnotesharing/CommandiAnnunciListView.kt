package com.example.clientnotesharing

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.dbLocale.dbHelper
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMD
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class CommandiAnnunciListView (var context: Context, var listaAnnunci: ArrayList<Annuncio>, var adapter: MyAdapter){

    fun fetchAnnunciFromServer(swipeLayout: SwipeRefreshLayout): ArrayList<Annuncio> {
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val response = NotesApi.retrofitService.getAnnunci()

                //uso i dati degli annunci
                if (response.isSuccessful) {
                    response.body()?.let { annunci ->
                        listaAnnunci.clear()
                        listaAnnunci.addAll(annunci) //questa la lista contiene tutti i dati degli annunci
                        adapter.updateData(listaAnnunci)

                        val database = dbHelper(context)
                        database.insertAnnunci(listaAnnunci, "UserTable")
                        adapter.updateData(database.getAllData("UserTable"))
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                handleNetworkException(e)
            } finally {
                swipeLayout.isRefreshing = false // Stop the refreshing animation
            }

        }
        return listaAnnunci
    }

    //recupera dal server i materiali corrispondenti all'annuncio selezionato (preso in input) ed invia/apre le classi corrispondenti
    fun clickMateriale(annuncioSelezionato: Annuncio){
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                if(annuncioSelezionato.tipoMateriale){ //materiale fisico
                    val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                    if(response.isSuccessful){
                        var materialeFisicoAssociato = response.body()

                        //serializzazione oggetto ricevuto e invio nella corrispondente classe
                        if(materialeFisicoAssociato != null){
                            val intent = Intent(context, AnnuncioMF::class.java)
                            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
                            val jsonStringM = Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato)

                            intent.putExtra("AnnuncioSelezionato", jsonStringA)
                            intent.putExtra("MaterialeAssociato", jsonStringM)
                            // Switch to the main thread before starting the activity
                            withContext(Dispatchers.Main) {
                                context.startActivity(intent)
                            }
                        }

                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
                }else{
                    val response = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                    if (response.isSuccessful){
                        var materialeDigitaleAssociato = response.body()
                        if(materialeDigitaleAssociato != null){
                            val intent = Intent(context, AnnuncioMD::class.java)
                            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
                            val jsonStringM = Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato)

                            intent.putExtra("AnnuncioSelezionato", jsonStringA)
                            intent.putExtra("MaterialeAssociato", jsonStringM)
                            // Switch to the main thread before starting the activity
                            withContext(Dispatchers.Main) {
                                context.startActivity(intent)
                            }
                        }
                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
                }

            } catch (e: HttpException) {
                Log.e("MainActivity", "HTTP Exception: ${e.message()}")
                e.printStackTrace()
                //DA GESTIRE!!!!!
            } catch (e: IOException) {
                Log.e("MainActivity", "IO Exception: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    // gestione delle eccezioni
    private fun handleNetworkException(e: Exception) {
        when (e) {
            is HttpException -> {
                Log.e("HomeFragment", "HTTP Exception: ${e.message()}")
            }
            is IOException -> {
                Log.e("HomeFragment", "IO Exception: ${e.message}")
            }
            else -> {
                Log.e("HomeFragment", "Exception: ${e.message}")
            }
        }
        e.printStackTrace()
    }

    fun searchListView(fragmentActivity: FragmentActivity, viewLifecycleOwner: LifecycleOwner){
        // Add MenuProvider to handle search functionality
        fragmentActivity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchManager = fragmentActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.setSearchableInfo(searchManager.getSearchableInfo(fragmentActivity.componentName))

                searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        searchView.clearFocus()
                        //searchView.setQuery("", false)
                        //searchView.isIconified = true
                        //Non ci servono, perchè non vogliamo fare nulla quando l'utente clicca enter
                        return true
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        adapter.filter.filter(newText)
                        return true
                    }
                })

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Handle other menu item selections if needed
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // Filtra (e toglie il filtro) per l'area
    fun setFilter(filterValue: String, statoFilter: Boolean): Boolean {
        if (!statoFilter) {
            adapter.filter.filter(filterValue)
            return true
        } else {
            adapter.filter.filter("")
            return false
        }
    }

}