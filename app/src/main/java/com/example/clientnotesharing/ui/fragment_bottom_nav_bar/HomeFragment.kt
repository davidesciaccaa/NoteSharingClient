package com.example.clientnotesharing.ui.fragment_bottom_nav_bar

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.clientnotesharing.util.CommandiAnnunciListView
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.ui.settings.SettingsActivity
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import com.example.clientnotesharing.util.Utility
import retrofit2.Response

/*
 * Classe per il fragment che contiene la listView per la visualizzazione degli annunci in Home
 */
class HomeFragment: Fragment(){
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var statoFilter: Boolean = false
    internal var listaAnnunci: ArrayList<Annuncio> = ArrayList()
    private lateinit var dbLocal: DbHelper
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val commandiAnnunci = CommandiAnnunciListView(requireContext())
        dbLocal = DbHelper(requireContext())
        adapter = MyAdapter(requireContext(), fetchAnnunciFromLocalDb())

        //Swipe for refresh
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            listaAnnunci = commandiAnnunci.fetchAnnunciFromServer(swipeLayout, listaAnnunci, ::opDbLocale, ::opServer) //aggiorno la lista degli annunci
        }

        // gestione della listView
        binding.listViewAnnunci.adapter = adapter
        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ -> // listener per i click degli elementi della listView
            if(listaAnnunci.isNotEmpty()){
                val clickedAnnuncio = listaAnnunci[position] // prendo l'elemento cliccato
                commandiAnnunci.clickMateriale(clickedAnnuncio) // chiamo il metodo per aprire l'activity corrispondente al materiale
            } else {
                listaAnnunci = fetchAnnunciFromLocalDb() //vengono presi dal db locale
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            }
        }

        // MenuProvider per la ricerca
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner, adapter)
        // MenuProvider per settings btn
        addSettingsBtn()

        // Filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
        binding.btnSport.setOnClickListener { setFilter("0") }
        binding.btnGiuridicoeconomico.setOnClickListener { setFilter("1") }
        binding.btnSanitario.setOnClickListener { setFilter("2") }
        binding.btnScienze.setOnClickListener { setFilter("3") }
        binding.btnUmanisticosociale.setOnClickListener { setFilter("4") }

        return root
    }

    // Metodo con le operazioni sul db locale che commandiAnnunci.fetchAnnunciFromServer deve eseguire
    private fun opDbLocale() {
        // Aggiungo anche nel db locale
        dbLocal.insertAnnunci(listaAnnunci)
        // Aggiorno la listView per visualizzare i nuovi dati
        adapter.updateData(dbLocal.getAllDataEccettoPersonali())
    }

    // Metodo con l'operazioni del server che commandiAnnunci.fetchAnnunciFromServer deve eseguire
    private suspend fun opServer(): Response<ArrayList<Annuncio>> {
        return NotesApi.retrofitService.getAnnunci(Utility().getUsername(requireContext()))
    }

    // Aggiunge all'app bar il bottone di settings
    private fun addSettingsBtn(){
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
        return ArrayList(dbHelper.getAllDataEccettoPersonali())
    }

    // Filtra (e toglie il filtro) per l'area
    private fun setFilter(filterValue: String) {
        if (!statoFilter) {
            adapter.filter.filter(filterValue)
            statoFilter = true
        } else {
            adapter.filter.filter("")
            statoFilter = false
        }
    }

}