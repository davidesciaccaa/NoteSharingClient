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
    override fun toString(): String {
        return when (this.areaMateriale) {
            1 -> "Area Sportiva"
            2 -> "Area Giuridico-Economica"
            3 -> "Area Sanitaria"
            4 -> "Area Scientifica"
            5 -> "Area delle Scienze Umane e Sociali"
            else -> "Unknown Area"
        }
    }
}