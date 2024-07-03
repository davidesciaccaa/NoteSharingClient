package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

/*
 * Data class per inviare/ricevera al/dal server i dati per i contenuti dei pdf
 */
@Serializable
class DatoDigitale (
    val idDato: String,
    val idAnnuncio: String,
    val fileBytes: ByteArray,
    val fileName: String
){
}