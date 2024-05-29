package com.example.clientnotesharing.ui.home

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMD
import com.example.clientnotesharing.ui.visualizza_materiale.AnnuncioMF
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import retrofit2.HttpException
import java.io.IOException

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val listViewAnnunci = binding.listViewAnnunci
        var listaAnnunci:ArrayList<Annuncio> = ArrayList<Annuncio>()
        //recupera tutta la lista degli annunci. Da vedere se ci sono problemi di performace
        lifecycleScope.launch {
            try {
                var response = NotesApi.retrofitService.getAnnunci()
                if(response.isSuccessful && response.body()!=null){
                    listaAnnunci = response.body()!!
                }else{
                    // Error occurred
                    val errorMessage = response.message()
                    // Handle error message...
                }
            } catch (e: HttpException) {
                Log.e("MainActivity", "HTTP Exception: ${e.message()}")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.e("MainActivity", "IO Exception: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }.invokeOnCompletion {
            //visualizzazione degli annunci nella listView
            val data = ArrayList<HashMap<String, Any>>()
            if (listaAnnunci.isNotEmpty()) {
                for (elem in listaAnnunci) {
                    val hm = HashMap<String, Any>()
                    hm["Tittle"] = elem.titolo
                    hm["Date"] = elem.data
                    data.add(hm)
                }
            }// se è vuota non succede nulla
            listViewAnnunci.adapter = SimpleAdapter(
                requireContext(), //nelle classi normali mettiamo this
                data,
                R.layout.listlayout,
                arrayOf("Tittle", "Date"),
                intArrayOf(R.id.textViewTittle, R.id.textViewData)  //si chiamano cosi quelli di simple_list_item_2
            )

            // listener per i click degli elementi della lista
            listViewAnnunci.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                val clickedAnnuncio = listaAnnunci[position]
                clickMateriale(clickedAnnuncio)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    //recupera dal server i materiali corrispondenti ed invia/apre le classi corrispondenti
    private fun clickMateriale(annuncioSelezionato: Annuncio){
        var materialeFisicoAssociato: MaterialeFisico? = null
        var materialeDigitaleAssociato: MaterialeDigitale? = null
        lifecycleScope.launch {
            try {
                if(annuncioSelezionato.tipoMateriale){ //materiale fisico
                    val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                    if(response.isSuccessful){
                        materialeFisicoAssociato = response.body()
                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
                }else{
                    val response = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                    if (response.isSuccessful){
                        materialeDigitaleAssociato = response.body()
                    }else{
                        // Error occurred
                        val errorMessage = response.message()
                        // Handle error message...
                    }
                }

            } catch (e: HttpException) {
                Log.e("MainActivity", "HTTP Exception: ${e.message()}")
                e.printStackTrace()
                //DA GESTIRE!!!!!
            } catch (e: IOException) {
                Log.e("MainActivity", "IO Exception: ${e.message}")
                e.printStackTrace()
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
                e.printStackTrace()
            }
        }.invokeOnCompletion {
            val intent = if (annuncioSelezionato.tipoMateriale) {
                Intent(requireContext(), AnnuncioMF::class.java)
            } else {
                Intent(requireContext(), AnnuncioMD::class.java)
            }
            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)

            val jsonStringM = if (annuncioSelezionato.tipoMateriale) {
                Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato!!) //non saraà null perchè finirà prima lo thread
            } else {
                Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato!!) //non saraà null perchè finirà prima lo thread
            }
            intent.putExtra("AnunncioSelezionato", jsonStringA)
            intent.putExtra("MaterialeAssociato", jsonStringM)
            startActivity(intent)
        }
    }

}