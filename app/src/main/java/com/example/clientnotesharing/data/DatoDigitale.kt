package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

@Serializable
class DatoDigitale (
    val idDato: String,
    val idAnnuncio: String,
    val fileBytes: ByteArray,
    val fileName: String
){
}