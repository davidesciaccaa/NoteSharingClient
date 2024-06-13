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
    fun AreaToString(): String {
        return when (this.areaMateriale) {
            0 -> "Area Sportiva"
            1 -> "Area Giuridico-Economica"
            2 -> "Area Sanitaria"
            3 -> "Area Scientifica"
            4 -> "Area delle Scienze Umane e Sociali"
            else -> "Unknown Area"
        }
    }
}