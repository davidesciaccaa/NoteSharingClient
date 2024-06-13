package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

@Serializable
data class MaterialeFisico(
    var id: String,
    var costo: Int,
    var annoRiferimento: Int,
    var areaMateriale: Int,
    var descrizioneMateriale: String,
    var comune: String,
    var provincia: String,
    var via: String,
    var numeroCivico: Int,
    var cap: Int
) {
    fun AreaToString(): String {
        return when (this.areaMateriale) {
            0 -> "Area Sportiva"
            1 -> "Area Giuridico-Economica"
            2 -> "Area Sanitaria"
            3 -> "Area Scientifica"
            4 -> "Area delle Scienze Umane e Sociali"
            else -> "Unknown Area ($areaMateriale)"
        }
    }
}