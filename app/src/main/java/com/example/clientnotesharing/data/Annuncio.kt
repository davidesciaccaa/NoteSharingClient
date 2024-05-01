package com.example.clientnotesharing.data
//import kotlinx.serialization.Serializable


//@Serializable
class Annuncio (
    val id: String,
    var data: String, //la data di creazione dell'annuncio
    var descrizioneAnnuncio: String,
    var idProprietario: String,
    var materialeD: MaterialeDigitale,
    var materialeF: MaterialeFisico
) { //la data è string perchè nessuno dei tipi di DATE supportano la serializzazione
    //in kotlin ci sono in automatico i getters e setters
}