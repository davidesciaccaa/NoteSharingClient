package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable


@Serializable
data class Annuncio (
    val id: String,
    val titolo: String,
    var data: String, //la data di creazione dell'annuncio
    var descrizioneAnnuncio: String,
    var idProprietario: String
) { //la data è string perchè nessuno dei tipi di DATE supportano la serializzazione
    //in kotlin ci sono in automatico i getters e setters
}