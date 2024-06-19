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
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.CommandiAnnunciListView
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

class HomeFragment: Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        var listaAnnunci: ArrayList<Annuncio> = ArrayList()

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val adapter = MyAdapter(requireContext(), fetchAnnunciFromDatabase())
        val commandiAnnunci = CommandiAnnunciListView(requireContext(), listaAnnunci, adapter)

        //Swipe for refresh
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            // Start a coroutine to call the suspend function
            commandiAnnunci.fetchAnnunciFromServer(swipeLayout)
        }

        binding.listViewAnnunci.adapter = adapter

        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            if(listaAnnunci.isNotEmpty()){
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            } else {
                Log.d("TAG", "I: Lista vuota")
            }
        }

        // Add MenuProvider to handle search functionality
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner)

        //filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
        binding.btnSport.setOnClickListener { commandiAnnunci.setFilter("0", false) }
        binding.btnGiuridicoeconomico.setOnClickListener { commandiAnnunci.setFilter("1", false) }
        binding.btnSanitario.setOnClickListener { commandiAnnunci.setFilter("2", false) }
        binding.btnScienze.setOnClickListener { commandiAnnunci.setFilter("3", false) }
        binding.btnUmanisticosociale.setOnClickListener { commandiAnnunci.setFilter("4", false) }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAnnunciFromDatabase(): ArrayList<Annuncio> {
        val dbHelper = dbHelper(requireContext())
        return ArrayList(dbHelper.getAllData("UserTable"))
    }

}