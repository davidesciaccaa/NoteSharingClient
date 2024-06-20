package com.example.clientnotesharing.ui.annunci_salvati

import android.content.Context
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
        val adapter = MyAdapter(requireContext(), fetchAnnunciPreferitiFromDatabase())

        val commandiAnnunci = CommandiAnnunciListView(requireContext(), adapter, false)
        //Swipe for refresh
        var swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            listaAnnunci = commandiAnnunci.fetchAnnunciPreferitiFromServer(getUsername() ,swipeLayout, listaAnnunci) //aggiorno la lista degli annunci

        }

        binding.listViewAnnunci.adapter = adapter
        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            if(listaAnnunci.isNotEmpty()){
                Log.d("TAG", "A: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            } else {
                listaAnnunci = fetchAnnunciPreferitiFromDatabase() //vengono presi dal db locale
                Log.d("TAG", "O: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
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

    private fun fetchAnnunciPreferitiFromDatabase(): ArrayList<Annuncio> {
        val dbHelper = dbHelper(requireContext())
        return ArrayList(dbHelper.getAnnunciPreferiti())
    }
    private fun getUsername(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if(username != null){
            return username
        }else return ""
    }
}