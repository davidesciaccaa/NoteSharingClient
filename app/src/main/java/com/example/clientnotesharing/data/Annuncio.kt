package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable


@Serializable
data class Annuncio (
    val id: String,
    val titolo: String,
    var data: String, //la data di creazione dell'annuncio
    var descrizioneAnnuncio: String,
    var tipoMateriale: Boolean, //se true e di tipo materiale fisico
    var idProprietario: String,
    var areaAnnuncio: Int
) { //la data è string perchè nessuno dei tipi di DATE supportano la serializzazione
    //in kotlin ci sono in automatico i getters e setters
    fun AreaToString(): String {
        return when (this.areaAnnuncio) {
            0 -> "Area Sportiva"
            1 -> "Area Giuridico-Economica"
            2 -> "Area Sanitaria"
            3 -> "Area Scientifica"
            4 -> "Area delle Scienze Umane e Sociali"
            else -> "Unknown Area ($areaAnnuncio)"
        }
    }
}