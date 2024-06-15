package com.example.clientnotesharing.ui.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.MyAdapter
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.dbHelper
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMD
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMF
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyAdapter
    private var listaAnnunci: ArrayList<Annuncio> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        //Swipe for refresh
        var swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            fetchAnnunciFromServer()
        }

        //
        adapter = MyAdapter(requireContext())
        binding.listViewAnnunci.adapter = adapter

        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            val clickedAnnuncio = listaAnnunci[position]
            clickMateriale(clickedAnnuncio)
        }

        // Add MenuProvider to handle search functionality
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.search_menu, menu)
                val searchManager = requireActivity().getSystemService(Context.SEARCH_SERVICE) as SearchManager
                val searchItem = menu.findItem(R.id.action_search)
                val searchView = searchItem.actionView as SearchView
                searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().componentName))

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

        return root
    }

    private fun fetchAnnunciFromServer() {
        lifecycleScope.launch {
            try {
                val response = NotesApi.retrofitService.getAnnunci()
                if (response.isSuccessful && response.body() != null) {
                    listaAnnunci = response.body()!!
                    adapter.updateData(listaAnnunci)
                } else {
                    val errorMessage = response.message()
                    Log.e("HomeFragment", "Error: $errorMessage")
                }
            } catch (e: HttpException) {
                Log.e("HomeFragment", "HTTP Exception: ${e.message()}")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("HomeFragment", "IO Exception: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("HomeFragment", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }.invokeOnCompletion {
            val database = dbHelper(requireContext())
            database.insertAnnunci(listaAnnunci)
            adapter.updateData(database.getAllData())
            binding.swipeLayout.isRefreshing = false // Stop the refreshing animation
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //recupera dal server i materiali corrispondenti ed invia/apre le classi corrispondenti
    private fun clickMateriale(annuncioSelezionato: Annuncio){
        var materialeFisicoAssociato: MaterialeFisico? = null
        var materialeDigitaleAssociato: MaterialeDigitale? = null
        lifecycleScope.launch {
            try {
                if(annuncioSelezionato.tipoMateriale){ //materiale fisico
                    val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                    if(response.isSuccessful){
                        materialeFisicoAssociato = response.body()
                        Log.d("TAG", "*****************Il nome è: ${materialeFisicoAssociato?.descrizioneMateriale}")
                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
                }else{
                    val response = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                    if (response.isSuccessful){
                        materialeDigitaleAssociato = response.body()
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
        }.invokeOnCompletion {
            val intent = if (annuncioSelezionato.tipoMateriale) {
                Intent(requireContext(), AnnuncioMF::class.java)
            } else {
                Intent(requireContext(), AnnuncioMD::class.java)
            }
            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)

            val jsonStringM = if (annuncioSelezionato.tipoMateriale) {
                Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato!!) //non sarà null perchè finirà prima lo thread
            } else {
                Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato!!) //non sarà null perchè finirà prima lo thread
            }
            intent.putExtra("AnnuncioSelezionato", jsonStringA)
            intent.putExtra("MaterialeAssociato", jsonStringM)
            startActivity(intent)
        }
    }



}