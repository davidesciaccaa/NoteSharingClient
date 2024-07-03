package com.example.clientnotesharing.ui.fragment_bottom_nav_bar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.util.CommandiAnnunciListView
import com.example.clientnotesharing.adapter.PersonaliAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentPersonaliBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import com.example.clientnotesharing.util.Utility
import retrofit2.Response

/*
 * Classe per il fragment che contiene la listView per la visualizzazione degli annunci personali dell'utente
 */
class AnnunciPersonaliFragment : Fragment() {

    private var _binding: FragmentPersonaliBinding? = null
    private val binding get() = _binding!!
    private var listaAnnunci: ArrayList<Annuncio> = ArrayList()
    private lateinit var adapter: PersonaliAdapter
    private lateinit var dbLocal: DbHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonaliBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val commandiAnnunci = CommandiAnnunciListView(requireContext())
        dbLocal = DbHelper(requireContext())

        // SwipeRefreshLayout
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            //listaAnnunci = fetchAnnunciFromRemoteServer(swipeLayout, dbLocal, listaAnnunci) // prende i dati dal server e aggiorna la lista
            listaAnnunci = commandiAnnunci.fetchAnnunciFromServer(swipeLayout, listaAnnunci, ::opDbLocale, ::opServer) // prende i dati dal server e aggiorna la lista

        }

        // gestione llistView
        adapter = PersonaliAdapter(requireContext(), fetchAnnunciFromLocalDb()) // inizializzazione adapter con i dati del db locale
        binding.listViewAnnunci.adapter = adapter // binding dei dati alla listView
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


        return root
    }
    // Metodo con le operazioni sul db locale che commandiAnnunci.fetchAnnunciFromServer deve eseguire
    private fun opDbLocale() {
        //Aggiungo anche nel db locale
        dbLocal.insertAnnunci(listaAnnunci)
        // Aggiorno la listView per visualizzare i nuovi dati
        adapter.updateData(dbLocal.getAnnunciPersonali())
    }
    // Metodo con l'operazioni del server che commandiAnnunci.fetchAnnunciFromServer deve eseguire
    private suspend fun opServer(): Response<ArrayList<Annuncio>> {
        return NotesApi.retrofitService.getMyAnnunci(Utility().getUsername(requireContext()))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }

    private fun fetchAnnunciFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = DbHelper(requireContext())
        return ArrayList(dbHelper.getAnnunciPersonali())
    }

}