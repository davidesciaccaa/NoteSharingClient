package com.example.clientnotesharing.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.CommandiAnnunciListView
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.ui.settings.SettingsActivity
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import com.example.clientnotesharing.util.Utility
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class HomeFragment: Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var statoFilter: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val dbLocal = DbHelper(requireContext())
        var listaAnnunci: ArrayList<Annuncio> = ArrayList()
        val adapter = MyAdapter(requireContext(), fetchAnnunciFromLocalDb())
        val commandiAnnunci = CommandiAnnunciListView(requireContext())


        //Swipe for refresh
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            //listaAnnunci = commandiAnnunci.fetchAnnunciFromServer(swipeLayout, listaAnnunci) //aggiorno la lista degli annunci
            listaAnnunci = fetchAnnunciFromServer(swipeLayout, listaAnnunci, adapter, dbLocal) //aggiorno la lista degli annunci
        }

        binding.listViewAnnunci.adapter = adapter

        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            if(listaAnnunci.isNotEmpty()){
                val clickedAnnuncio = listaAnnunci[position] // è come un get
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            } else {
                //Log.d("TAG", "I: Lista vuota")
                listaAnnunci = fetchAnnunciFromLocalDb() //vengono presi dal db locale
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            }
        }

        // Add MenuProvider to handle search functionality
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner, adapter)

        // Filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
        binding.btnSport.setOnClickListener { setFilter("0", adapter) }
        binding.btnGiuridicoeconomico.setOnClickListener { setFilter("1", adapter) }
        binding.btnSanitario.setOnClickListener { setFilter("2", adapter) }
        binding.btnScienze.setOnClickListener { setFilter("3", adapter) }
        binding.btnUmanisticosociale.setOnClickListener { setFilter("4", adapter) }

        addSettingsBtn() //Per avere il bottone di settings
        return root
    }
    fun addSettingsBtn(){
        // Add MenuProvider to handle search functionality
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.settings_menu, menu)

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_settings -> {
                        val intent = Intent(requireContext(), SettingsActivity::class.java)
                        startActivity(intent)
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAnnunciFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = DbHelper(requireContext())
        return ArrayList(dbHelper.getAllDataEccettoPersonali("UserTable"))
    }

    private fun fetchAnnunciFromServer(swipeLayout: SwipeRefreshLayout, listaAnnunci: ArrayList<Annuncio>, adapter: MyAdapter, dbLocal: DbHelper): ArrayList<Annuncio> {
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val response = NotesApi.retrofitService.getAnnunci(Utility().getUsername(requireContext()))

                //uso i dati degli annunci
                if (response.isSuccessful) {
                    response.body()?.let { annunci ->
                        listaAnnunci.clear()
                        listaAnnunci.addAll(annunci) //questa la lista contiene tutti i dati degli annunci
                        //adapter.updateData(listaAnnunci)

                        dbLocal.insertAnnunci(listaAnnunci, "UserTable")
                        adapter.updateData(dbLocal.getAllDataEccettoPersonali("UserTable"))
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
    // Filtra (e toglie il filtro) per l'area
    private fun setFilter(filterValue: String, adapter: MyAdapter) {
        if (!statoFilter) {
            adapter.filter.filter(filterValue)
            statoFilter = true
        } else {
            adapter.filter.filter("")
            statoFilter = false
        }
    }
}