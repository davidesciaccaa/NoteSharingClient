package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

/*
 * Data class per inviare/ricevera al/dal server i dati di riferimento per i materiali fisici (degli annunci)
 */
@Serializable
data class MaterialeFisico(
    var id: String,
    var costo: Int,
    var annoRiferimento: Int,
    var descrizioneMateriale: String,
    var comune: String,
    var provincia: String,
    var via: String,
    var numeroCivico: Int,
    var cap: Int
) {

}