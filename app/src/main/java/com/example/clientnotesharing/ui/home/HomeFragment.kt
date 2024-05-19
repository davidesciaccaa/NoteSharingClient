package com.example.clientnotesharing.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.AnnuncioMD
import com.example.clientnotesharing.AnnuncioMF
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
        var listaAnnunci = ArrayList<Annuncio>()
        //recupera tutta la lista degli annunci. Da vedere se ci sono problemi di performace
        lifecycleScope.launch {
            try {
                listaAnnunci = NotesApi.retrofitService.getAnnunci()
                /*
                listaAnnunci.add(Annuncio("fevevefv", "Titolo Prova 1", "22-01-2023", "Android is a mobile operating system based on a modified version of the Linux kernel and other open-source software, designed primarily for touchscreen mobile devices such as smartphones and tablets. Android is developed by a consortium of developers known as the Open Handset Alliance, though its most widely used version is primarily developed by Google. It was unveiled in November 2007, with the first commercial Android device, the HTC Dream, being launched in September 2008.\n" +
                        "\n" +
                        "At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google, which ships with additional proprietary closed-source software pre-installed,[6] most notably Google Mobile Services (GMS)[7] which includes core apps such as Google Chrome, the digital distribution platform Google Play, and the associated Google Play Services development platform. Firebase Cloud Messaging is used for push notifications. While AOSP is free, the name and logo are trademarks of Google, which imposes standards to restrict the use of Android branding by devices outside their ecosystem.", true, "email@chfbv"))
                listaAnnunci.add(Annuncio("fevevefv", "Titolo Prova 2", "12-01-2025", "ddescrizionehjfbhvbfbvhfe", true,"email@chfbv"))
                */
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
            for (i in 0..listaAnnunci.size - 1){
                val hm = HashMap<String, Any>()
                var elem = listaAnnunci.get(i)
                hm["Tittle"] = elem.titolo
                hm["Date"] = elem.data
                data.add(hm)
            }
            listViewAnnunci.adapter = SimpleAdapter(
                requireContext(), //nelle classi normali mettiamo this
                data,
                R.layout.listlayout,
                arrayOf("Tittle", "Date"),
                intArrayOf(R.id.textViewTittle, R.id.textViewData)  //si chiaano cosi quelli di simple_list_item_2
            )

            // listener per i click degli elementi della lista
            listViewAnnunci.setOnItemClickListener { parent, view, position, id ->
                // Handle item click here
                val clickedAnnuncio = listaAnnunci[position]
                if(clickedAnnuncio.tipoMateriale){
                    clickMF(clickedAnnuncio)
                }else{
                    clickMD(clickedAnnuncio)
                }


            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun clickMF(annuncioSelezionato: Annuncio){
        var materialeFisicoAssociato: MaterialeFisico? = null
        lifecycleScope.launch {
            try {
                materialeFisicoAssociato = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                //materialeFisicoAssociato = MaterialeFisico("id2r43", 2, 2024, "algotitmi", "descriione: At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google", "Varese", "Varese", "Viale Aguggiari", 169, 21100)
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
            val intent = Intent(requireContext(), AnnuncioMF::class.java)
            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
            val jsonStringMF = Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato!!) //non saraà null perchè finirà prima lo thread
            intent.putExtra("AnunncioSelezionato", jsonStringA)
            intent.putExtra("MaterialeFisicoAssociato", jsonStringMF)
            startActivity(intent)
        }
    }
    private fun clickMD(annuncioSelezionato: Annuncio){
        var materialeDigitaleAssociato: MaterialeDigitale? = null
        lifecycleScope.launch {
            try {
                materialeDigitaleAssociato = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                //materialeFisicoAssociato = MaterialeFisico("id2r43", 2, 2024, "algotitmi", "descriione: At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google", "Varese", "Varese", "Viale Aguggiari", 169, 21100)
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
            val intent = Intent(requireContext(), AnnuncioMD::class.java)
            val jsonStringA = Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
            val jsonStringMF = Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato!!) //non saraà null perchè finirà prima lo thread
            intent.putExtra("AnunncioSelezionato", jsonStringA)
            intent.putExtra("MaterialeDigitaleAssociato", jsonStringMF)
            startActivity(intent)
        }

    }


    private fun clickMateriale(annuncioSelezionato: Annuncio){
        var materialeFisicoAssociato: MaterialeFisico? = null
        var materialeDigitaleAssociato: MaterialeDigitale? = null
        lifecycleScope.launch {
            try {
                if(annuncioSelezionato.tipoMateriale){ //materiale fisico
                    materialeFisicoAssociato = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncioSelezionato.id)
                    //materialeFisicoAssociato = MaterialeFisico("id2r43", 2, 2024, "algotitmi", "descriione: At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google", "Varese", "Varese", "Viale Aguggiari", 169, 21100)
                }else{
                    materialeDigitaleAssociato = NotesApi.retrofitService.getMaterialeDigitaleAnnuncio(annuncioSelezionato.id)
                    //materialeFisicoAssociato = MaterialeFisico("id2r43", 2, 2024, "algotitmi", "descriione: At its core, the operating system is known as the Android Open Source Project (AOSP)[5] and is free and open-source software (FOSS) primarily licensed under the Apache License. However, most devices run on the proprietary Android version developed by Google", "Varese", "Varese", "Viale Aguggiari", 169, 21100)
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
            val intent = if (annuncioSelezionato.tipoMateriale) {
                Intent(requireContext(), AnnuncioMF::class.java)
            } else {
                Intent(requireContext(), AnnuncioMD::class.java)
            }
            val jsonStringA = if (annuncioSelezionato.tipoMateriale) {
                Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
            } else {
                Json.encodeToString(Annuncio.serializer(), annuncioSelezionato)
            }
            val jsonStringM = if (annuncioSelezionato.tipoMateriale) {
                Json.encodeToString(MaterialeFisico.serializer(), materialeFisicoAssociato!!) //non saraà null perchè finirà prima lo thread
            } else {
                Json.encodeToString(MaterialeDigitale.serializer(), materialeDigitaleAssociato!!) //non saraà null perchè finirà prima lo thread
            }
            intent.putExtra("AnunncioSelezionato", jsonStringA)
            intent.putExtra("MaterialeDigitaleAssociato", jsonStringM)
            startActivity(intent)

        }
    }
}