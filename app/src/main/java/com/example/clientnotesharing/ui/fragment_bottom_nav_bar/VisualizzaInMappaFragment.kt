package com.example.clientnotesharing.ui.fragment_bottom_nav_bar

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.HandlerThread
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.util.CommandiAnnunciListView
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.databinding.FragmentVisualizzaMappaBinding
import com.example.clientnotesharing.util.Utility
import com.tomtom.quantity.Distance
import com.tomtom.sdk.location.GeoLocation
import com.tomtom.sdk.location.LocationProvider
import com.tomtom.sdk.location.OnLocationUpdateListener
import com.tomtom.sdk.location.android.AndroidLocationProvider
import com.tomtom.sdk.location.android.AndroidLocationProviderConfig
import com.tomtom.sdk.map.display.TomTomMap
import com.tomtom.sdk.map.display.image.ImageFactory
import com.tomtom.sdk.map.display.location.LocationMarkerOptions
import com.tomtom.sdk.map.display.marker.MarkerOptions
import com.tomtom.sdk.map.display.ui.MapFragment
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds
import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.dbLocale.DbHelper
import com.tomtom.sdk.map.display.marker.Marker

/*
 * Classe per il fragment che contiene la mappa per la visualizzazione della posizione di ritiro
 * di tutti gli annunci con materiali fisici
 */
class VisualizzaInMappaFragment : Fragment() {
    private val LOCATION_PERMISSION_REQUEST_CODE = 100
    private var _binding: FragmentVisualizzaMappaBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVisualizzaMappaBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Controllo dei permessi di location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // I permessi ci sono
            val myMap = childFragmentManager.findFragmentById(binding.mapFragment.id) as? MapFragment
            if (myMap != null) {
                myMap.getMapAsync { tomtomMap: TomTomMap ->
                    // Ottiene il location provider
                    Utility().setupLocationProvider(tomtomMap, requireContext())
                    // Riceve tutti gli annunci (eccetto i personali)
                    val listaAnnunci = DbHelper(requireContext()).getAllDataEccettoPersonali()
                    // Aggiunge merker per ogni annuncio
                    addMarkerToMap(tomtomMap, listaAnnunci)

                    // Quando si fa click su un Marker nella mappa si apre la view ce mostra i corrispondenti dati
                    tomtomMap.addMarkerClickListener { marker: Marker ->
                        val idAnnuncio = marker.tag
                        //Recupero dal server
                        if (idAnnuncio!=null) {
                            val annuncio = trovaAnnuncioPerId(listaAnnunci, idAnnuncio)
                            if (annuncio != null) {
                                CommandiAnnunciListView(requireContext()).clickMateriale(annuncio)
                            }
                        }
                    }
                }
            }
        }
        return root
    }
    // Metodo che aggiunge un marker per ogni annuncio nella lista
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun addMarkerToMap(tomtomMap: TomTomMap, listaAnnunci: ArrayList<Annuncio>) {
        binding.btnRefresh.setOnClickListener{
            // Aggiunge marker per ogni annuncio esistente, dalla internal var di HomeFragment
            for (annuncio in listaAnnunci) {
                // Controlliamo se è un materiale fisico, se è true signiica che è fisico (è boolean)
                if (annuncio.tipoMateriale) {
                    lifecycleScope.launch {
                        val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncio.id)
                        if(response.isSuccessful){
                            val materialeFisico = response.body()
                            // Otteniamo le coordinate dall'indirizzo
                            val geoPoint = Utility().getGeoPointFromAddress(requireContext(), "${materialeFisico?.comune}, via ${materialeFisico?.via}, ${materialeFisico?.numeroCivico}")
                            if (geoPoint != null) {
                                val markerOptions =
                                    MarkerOptions(
                                        coordinate = geoPoint,
                                        pinImage = ImageFactory.fromResource(R.drawable.map_pin),
                                        tag = annuncio.id
                                    )
                                tomtomMap.addMarker(markerOptions)
                            } else {
                                Log.e("GeoPoint", "Indirizzo non trovato")
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    fun trovaAnnuncioPerId(listaAnnunci: ArrayList<Annuncio>, idDaCercare: String): Annuncio? {
        return listaAnnunci.find { it.id == idDaCercare }
    }

}