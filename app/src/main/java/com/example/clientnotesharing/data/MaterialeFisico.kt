package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

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