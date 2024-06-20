package com.example.clientnotesharing.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clientnotesharing.CommandiAnnunciListView
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.dbHelper

class HomeFragment: Fragment(){

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var listaAnnunci: ArrayList<Annuncio> = ArrayList()
        val adapter = MyAdapter(requireContext(), fetchAnnunciFromLocalDb())
        val commandiAnnunci = CommandiAnnunciListView(requireContext(), adapter, false)

        //Swipe for refresh
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            listaAnnunci = commandiAnnunci.fetchAnnunciFromServer(swipeLayout, listaAnnunci) //aggiorno la lista degli annunci
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
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner)

        //filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
        binding.btnSport.setOnClickListener { commandiAnnunci.setFilter("0") }
        binding.btnGiuridicoeconomico.setOnClickListener { commandiAnnunci.setFilter("1") }
        binding.btnSanitario.setOnClickListener { commandiAnnunci.setFilter("2") }
        binding.btnScienze.setOnClickListener { commandiAnnunci.setFilter("3") }
        binding.btnUmanisticosociale.setOnClickListener { commandiAnnunci.setFilter("4") }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAnnunciFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = dbHelper(requireContext())
        return ArrayList(dbHelper.getAllData("UserTable"))
    }

}