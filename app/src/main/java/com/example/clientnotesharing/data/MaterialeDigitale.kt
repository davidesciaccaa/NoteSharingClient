package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class MaterialeDigitale(
    var id: String,
    var annoRiferimento: Int,
    var areaMateriale: Int,
    var descrizioneMateriale: String
) {
    //in kotlin ci sono in automatico i getters e setters
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