package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class MaterialeDigitale(
    var annoRiferimento: Int,
    var nomeCorso: String,
    var descrizioneMateriale: String,
) {
    //in kotlin ci sono in automatico i getters e setters
}