package com.example.clientnotesharing.ui.annunci_salvati

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.clientnotesharing.CommandiAnnunciListView
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.adapter.MyAdapter
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import com.example.clientnotesharing.dbLocale.DbHelper
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException


class AnnunciSalvatiFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var statoFilter: Boolean = false
    private lateinit var adapter: MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val dashboardViewModel =
            ViewModelProvider(this)[AnnunciSalvatiViewModel::class.java]

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val dbLocal = DbHelper(requireContext())
        var listaAnnunci: ArrayList<Annuncio> = ArrayList()
        adapter = MyAdapter(requireContext(), fetchAnnunciPreferitiFromLocalDb())

        val commandiAnnunci = CommandiAnnunciListView(requireContext())
        //Swipe for refresh
        var swipeLayout = binding.swipeLayout
        swipeLayout.setOnRefreshListener {
            //listaAnnunci = commandiAnnunci.fetchAnnunciPreferitiFromServer(getUsername() ,swipeLayout, listaAnnunci) //aggiorno la lista degli annunci
            listaAnnunci = fetchAnnunciPreferitiFromServer(getUsername() ,swipeLayout, listaAnnunci, adapter, dbLocal) //aggiorno la lista degli annunci
        }

        binding.listViewAnnunci.adapter = adapter
        binding.listViewAnnunci.setOnItemClickListener { _, _, position, _ ->
            if(listaAnnunci.isNotEmpty() && position in listaAnnunci.indices){
                Log.d("TAG", "A: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            } else {
                listaAnnunci = fetchAnnunciPreferitiFromLocalDb() //vengono presi dal db locale
                Log.d("TAG", "O: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")
                val clickedAnnuncio = listaAnnunci[position]
                commandiAnnunci.clickMateriale(clickedAnnuncio)
            }
        }

        // Add MenuProvider to handle search functionality
        commandiAnnunci.searchListView(requireActivity(), viewLifecycleOwner, adapter)

        //filtraggio per area - possiamo usare i bottoni per cambiare area degli annunci in Home
        binding.btnSport.setOnClickListener { setFilter("0") }
        binding.btnGiuridicoeconomico.setOnClickListener { setFilter("1") }
        binding.btnSanitario.setOnClickListener { setFilter("2") }
        binding.btnScienze.setOnClickListener { setFilter("3") }
        binding.btnUmanisticosociale.setOnClickListener { setFilter("4") }


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun fetchAnnunciPreferitiFromLocalDb(): ArrayList<Annuncio> {
        val dbHelper = DbHelper(requireContext())
        return ArrayList(dbHelper.getAnnunciPreferiti())
    }
    private fun getUsername(): String {
        val sharedPreferences = requireContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        var username = sharedPreferences.getString("username", null)
        if(username != null){
            return username
        }else return ""
    }
    fun fetchAnnunciPreferitiFromServer(username:String, swipeLayout: SwipeRefreshLayout, listaAnnunci: ArrayList<Annuncio>, adapter: MyAdapter, dbLocal: DbHelper): ArrayList<Annuncio> {
        (context as? LifecycleOwner)?.lifecycleScope?.launch {
            try {
                val response = NotesApi.retrofitService.getAnnunciSalvati(username)

                //uso i dati degli annunci
                if (response.isSuccessful) {
                    response.body()?.let { annunci ->
                        listaAnnunci.clear()
                        listaAnnunci.addAll(annunci) //questa la lista contiene tutti i dati degli annunci

                        dbLocal.insertAnnunci(listaAnnunci, "UserFavoritesTable")
                        adapter.updateData(dbLocal.getAnnunciPreferiti())
                        //var list = database.getAllData("UserFavoritesTable")
                        //Log.d("TAG", "I: +++++++++++++++++++++++ ${listaAnnunci.get(0)}")

                    }
                } else {
                    Log.e("", "Error: ${response.message()}")
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