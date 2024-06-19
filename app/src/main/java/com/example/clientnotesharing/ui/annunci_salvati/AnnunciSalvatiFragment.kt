package com.example.clientnotesharing.ui.annunci_salvati

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clientnotesharing.CommandiAnnunciListView
import com.example.clientnotesharing.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.dbHelper

class AnnunciSalvatiFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[AnnunciSalvatiViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var listaAnnunci: ArrayList<Annuncio> = ArrayList()
        val adapter = MyAdapter(requireContext(), fetchAnnunciFromDatabase())

        val commandiAnnunci = CommandiAnnunciListView(requireContext(), adapter)
        //Swipe for refresh
        var swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            listaAnnunci = commandiAnnunci.fetchAnnunciSalvatiFromServer(swipeLayout, listaAnnunci) //aggiorno la lista degli annunci

        }

        binding.listViewAnnunci.adapter = adapter
        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            if(listaAnnunci.isNotEmpty()){
                Log.d("TAG", "A: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            } else {
                listaAnnunci = fetchAnnunciFromDatabase() //vengono presi dal db locale
                Log.d("TAG", "O: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
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
        return ArrayList(dbHelper.getAllData("UserFavoritesTable"))
    }
}