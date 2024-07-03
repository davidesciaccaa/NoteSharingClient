package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

/*
 * Data class per inviare/ricevera al/dal server i dati di riferimento per i materiali digitali (degli annunci)
 */
@Serializable
data class MaterialeDigitale(
    var id: String,
    var annoRiferimento: Int,
    var descrizioneMateriale: String
) {
}