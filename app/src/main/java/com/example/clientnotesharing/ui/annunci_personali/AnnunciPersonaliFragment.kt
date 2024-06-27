package com.example.clientnotesharing.ui.annunci_personali

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.CommandiAnnunciListView
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.adapter.PersonaliAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentPersonaliBinding
import com.example.clientnotesharing.dbLocale.dbHelper
import kotlinx.coroutines.launch

class AnnunciPersonaliFragment : Fragment() {

    private var _binding: FragmentPersonaliBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: PersonaliAdapter
    private var listaAnnunci: ArrayList<Annuncio> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonaliBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val dbLocal = dbHelper(requireContext())
        // Setup SwipeRefreshLayout
        val swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            listaAnnunci = fetchAnnunciFromRemoteServer(swipeLayout, dbLocal)
        }

        // Initialize ArrayAdapter
        adapter = PersonaliAdapter(requireContext(), fetchAnnunciFromLocalDb())
        binding.listViewAnnunci.adapter = adapter
        val commandiAnnunci = CommandiAnnunciListView(requireContext())
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


        // Initial fetch from local DB or any other initial data source
        // listaAnnunci.addAll(fetchAnnunciFromLocalDb())

        return root
    }

    private fun fetchAnnunciFromRemoteServer(swipeLayout: SwipeRefreshLayout, dbLocal: dbHelper): ArrayList<Annuncio> {
        swipeLayout.isRefreshing = true // Start refreshing animation

        lifecycleScope.launch {
            try {
                val response = NotesApi.retrofitService.getMyAnnunci(getUsername())

                if (response.isSuccessful) {
                    response.body()?.let { annunci ->
                        listaAnnunci.clear()
                        listaAnnunci.addAll(annunci)
                        adapter.updateData(listaAnnunci)

                        // Aggiunta anche nel db locale
                        dbLocal.insertAnnunci(listaAnnunci, "UserTable")
                        //aggiorno la listView
                        adapter.updateData(dbLocal.getAllData("UserTable"))
                    }
                } else {
                    Log.e("AnnunciPersonaliFragment", "Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("AnnunciPersonaliFragment", "Error fetching data", e)
                // Handle network or API call exception
            } finally {
                swipeLayout.isRefreshing = false // Stop refreshing animation
            }
        }
        return listaAnnunci
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Prevent memory leaks
    }


    private fun fetchAnnunciFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = dbHelper(requireContext())
        return ArrayList(dbHelper.getAllData("UserTable"))
    }

    private fun getUsername(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if(username != null){
            return username
        }else return ""
    }


}