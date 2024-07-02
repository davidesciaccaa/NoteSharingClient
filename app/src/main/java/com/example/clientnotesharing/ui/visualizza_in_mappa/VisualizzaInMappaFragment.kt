package com.example.clientnotesharing.ui.visualizza_in_mappa

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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.clientnotesharing.NotesApi
import com.example.clientnotesharing.R
import com.example.clientnotesharing.databinding.FragmentVisualizzaMappaBinding
import com.example.clientnotesharing.ui.home.HomeFragment
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
        val notificationsViewModel =
            ViewModelProvider(this)[VisualizzaInMappaViewModel::class.java]
        _binding = FragmentVisualizzaMappaBinding.inflate(inflater, container, false)
        val root: View = binding.root
        // Permessi di location
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            val myMap = childFragmentManager.findFragmentById(binding.mapFragment.id) as? MapFragment
            Log.e("HomeFragment", "Inizio...")
            if (myMap != null) {
                Log.e("HomeFragment", "dentro if...")
                myMap.getMapAsync { tomtomMap: TomTomMap ->
                    //Location provider
                    val androidLocationProviderConfig =
                        AndroidLocationProviderConfig(
                            minTimeInterval = 250L.milliseconds,
                            minDistance = Distance.meters(20.0),
                        )
                    val locationHandlerThread = HandlerThread("locationHandlerThread")
                    locationHandlerThread.start()
                    val androidLocationProvider: LocationProvider =
                        AndroidLocationProvider(
                            context = requireContext(),
                            locationLooper = locationHandlerThread.looper,
                            config = androidLocationProviderConfig,
                        )
                    // location attuale
                    tomtomMap.setLocationProvider(androidLocationProvider)
                    androidLocationProvider.enable()
                    val locationMarkerOptions =
                        LocationMarkerOptions(
                            type = LocationMarkerOptions.Type.Pointer,
                        )
                    tomtomMap.enableLocationMarker(locationMarkerOptions)
                    val onLocationUpdateListener =
                        OnLocationUpdateListener { location: GeoLocation ->
                            /* YOUR CODE GOES HERE */
                        }
                    androidLocationProvider.addOnLocationUpdateListener(onLocationUpdateListener)
                    androidLocationProvider.removeOnLocationUpdateListener(onLocationUpdateListener)

                    binding.btnRefresh.setOnClickListener{
                        // Aggiunge marker per ogni annuncio esistente, dalla internal var di HomeFragment
                        for (annuncio in DbHelper(requireContext()).getAllDataEccettoPersonali("UserTable")) {
                            // Controlliamo se è un materiale fisico, se è true lo è (è boolean)
                            if (annuncio.tipoMateriale) {
                                lifecycleScope.launch {
                                    val response = NotesApi.retrofitService.getMaterialeFisicoAnnuncio(annuncio.id)
                                    if(response.isSuccessful){
                                        val materialeFisico = response.body()
                                        // Dobbiamo passare l'indirizzo dell'annuncio
                                        val geoPoint = Utility().getGeoPointFromAddress(requireContext(), "${materialeFisico?.comune}, via ${materialeFisico?.via}, ${materialeFisico?.numeroCivico}")
                                        // Use geoPoint here, it will be null if no address was found
                                        Log.e("HomeFragment", "coordinate $geoPoint")
                                        if (geoPoint != null) {
                                            val markerOptions =
                                                MarkerOptions(
                                                    coordinate = geoPoint,
                                                    pinImage = ImageFactory.fromResource(R.drawable.map_pin),
                                                )
                                            tomtomMap.addMarker(markerOptions)
                                            Log.e("HomeFragment", "Fine")
                                        } else {
                                            // Handle the case where no addresses were found
                                            Log.e("GeoPoint", "Address not found")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Dopo if-else
        return root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}