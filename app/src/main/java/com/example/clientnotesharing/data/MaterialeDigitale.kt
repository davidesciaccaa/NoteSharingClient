package com.example.clientnotesharing.data
//import kotlinx.serialization.Serializable

//@Serializable
class MaterialeDigitale(
    var annoRiferimento: Int,
    var nomeCorso: String,
    var descrizioneMateriale: String,
    var datoDigitale: ByteArray //Dovrà essere inviato in streaming
) {
    //in kotlin ci sono in automatico i getters e setters
}