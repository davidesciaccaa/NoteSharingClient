package com.example.clientnotesharing.ui.fragment_bottom_nav_bar

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.util.CommandiAnnunciListView
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import com.example.clientnotesharing.util.Utility
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException

/*
 * Classe per il fragment che contiene la listView per la visualizzazione degli annunci salvati come preferiti
 */
class AnnunciSalvatiFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var statoFilter: Boolean = false
    private var listaAnnunci: ArrayList<Annuncio> = ArrayList()
    private lateinit var adapter: MyAdapter //Perchè ha bisogno del context e requireContext non puo essere fatto qua
    private lateinit var dbLocal: DbHelper //Perchè ha bisogno del context e requireContext non puo essere fatto qua

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val commandiAnnunci = CommandiAnnunciListView(requireContext())
        dbLocal = DbHelper(requireContext())
        adapter= MyAdapter(requireContext(), fetchAnnunciPreferitiFromLocalDb()) // inizializzazione adapter con i dati del db locale

        // SwipeRefreshLayout
        var swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            //listaAnnunci = fetchAnnunciPreferitiFromServer(getUsername() ,swipeLayout, listaAnnunci, adapter, dbLocal) // prende i dati dal server e aggiorna la lista
            listaAnnunci = commandiAnnunci.fetchAnnunciFromServer(swipeLayout, listaAnnunci, ::opDbLocale, ::opServer) // prende i dati dal server e aggiorna la lista

        }

        // gestione della listView
        binding.listViewAnnunci.adapter = adapter
        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ -> // listener per i click degli elementi della listView
            if(listaAnnunci.isNotEmpty() && position in listaAnnunci.indices){
                val clickedAnnuncio = listaAnnunci[position] // prendo l'elemento cliccato
                commandiAnnunci.clickMateriale(clickedAnnuncio) // chiamo il metodo per aprire l'activity corrispondente al materiale
            } else {
                listaAnnunci = fetchAnnunciPreferitiFromLocalDb() //vengono presi dal db locale
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            }
        }

        // Gestione della ricerca
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner, adapter)

        //filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
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
        adapter.updateData(dbLocal.getAnnunciPreferiti())
    }

    // Metodo con l'operazioni del server che commandiAnnunci.fetchAnnunciFromServer deve eseguire
    private suspend fun opServer(): Response<ArrayList<Annuncio>> {
        return NotesApi.retrofitService.getAnnunciSalvati(Utility().getUsername(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAnnunciPreferitiFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = DbHelper(requireContext())
        return ArrayList(dbHelper.getAnnunciPreferiti())
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
    fun setFilter(filterValue: String) {
        if (!statoFilter) {
            adapter.filter.filter(filterValue)
            statoFilter = true
        } else {
            adapter.filter.filter("")
            statoFilter = false
        }
    }
}