package com.example.clientnotesharing.util

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMD
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import kotlin.reflect.KSuspendFunction0

/*
 * Classe generale che offre metodi utili per gestire le listView
 */
class CommandiAnnunciListView (var context: Context){

    /*
     * Metodo che recupera dal server il materiale corrispondenti all'annuncio selezionato (preso in input)
     * e apre la corrispondente classe per la visualizzazione
     */
    fun clickMateriale(annuncioSelezionato: Annuncio){
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                if(annuncioSelezionato.tipoMateriale){ //materiale fisico
                    val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                    if(response.isSuccessful){
                        val materialeFisicoAssociato = response.body()
                        if(materialeFisicoAssociato != null){
                            openClasseVisualizzaMF(annuncioSelezionato, materialeFisicoAssociato)
                        }
                    }else{
                        // Errore
                        Log.d("ComandiAnnunciListView, fun clickMateriale", "ComandiAnnunciListView, fun clickMateriale: Error: Response from server ${response.message()}")
                    }
                }else{
                    val response = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                    if (response.isSuccessful){
                        val materialeDigitaleAssociato = response.body()
                        if(materialeDigitaleAssociato != null){
                            openClasseVisualizzaMD(annuncioSelezionato, materialeDigitaleAssociato)
                        }
                    }else{
                        Log.d("ComandiAnnunciListView, fun clickMateriale", "ComandiAnnunciListView, fun clickMateriale: Error: Response from server ${response.message()}")
                    }
                }


            } catch (e: HttpException) {
                Log.e("MainActivity", "HTTP Exception: ${e.message}")
            } catch (e: IOException) {
                Log.e("MainActivity", "IO Exception: ${e.message}")
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
            }
        }
    }

    // Metodo che serializza i dati dell'annuncio (compresi del materiale fisico associato) ed apre la classe di visualizzazione
    private suspend fun openClasseVisualizzaMD(annuncioSelezionato: Annuncio, materialeDigitaleAssociato: MaterialeDigitale) {
        val intent = Intent(context, AnnuncioMD::class.java)
        val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
        val jsonStringM = Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato)
        intent.putExtra("AnnuncioSelezionato", jsonStringA)
        intent.putExtra("MaterialeAssociato", jsonStringM)
        // prima di aprire la nuova activity passo al thread main
        withContext(Dispatchers.Main) {
            context.startActivity(intent)
        }
    }

    // Metodo che serializza i dati dell'annuncio (compresi del materiale digitale associato) ed apre la classe di visualizzazione
    private suspend fun openClasseVisualizzaMF(annuncioSelezionato: Annuncio, materialeFisicoAssociato: MaterialeFisico) {
        // serializzazione dell'oggetto ricevuto e invio nella corrispondente classe
        val intent = Intent(context, AnnuncioMF::class.java)
        val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
        val jsonStringM = Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato)
        intent.putExtra("AnnuncioSelezionato", jsonStringA)
        intent.putExtra("MaterialeAssociato", jsonStringM)
        // prima di aprire la nuova activity passo al thread main
        withContext(Dispatchers.Main) {
            context.startActivity(intent)
        }
    }

    // Metodo che gestisce il bottone della ricerca nell'app bar
    fun searchListView(fragmentActivity: FragmentActivity, viewLifecycleOwner: LifecycleOwner, adapter: MyAdapter){
        fragmentActivity.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchManager = fragmentActivity.getSystemService(Context.SEARCH_SERVICE) as SearchManager
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.setSearchableInfo(searchManager.getSearchableInfo(fragmentActivity.componentName))

                // le query di ricerca
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
                // Non facciamo nulla
                return false
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    // Metodo che recupera dal server gli annunci e aggiorna anche il db locale
    fun fetchAnnunciFromServer(
        swipeLayout: SwipeRefreshLayout,
        listaAnnunci: ArrayList<Annuncio>,
        operazioniDbLocale: () -> Unit, // cosa specifica da fare
        serverRequest: KSuspendFunction0<Response<ArrayList<Annuncio>>>
    ): ArrayList<Annuncio> {
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val response = serverRequest()
                if (response.isSuccessful) {
                    response.body()?.let { annunci ->
                        // Aggiorno la lista
                        listaAnnunci.clear()
                        listaAnnunci.addAll(annunci)
                        operazioniDbLocale()
                    }
                } else {
                    Log.e("HomeFragment", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("HomeFragment", "${e.message}")
            } finally {
                swipeLayout.isRefreshing = false // Stop the refreshing animation
            }

        }
        return listaAnnunci
    }



}