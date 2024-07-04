package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

/*
 * Classe per ricever/inviare i dati per aggiungere/rimuovere un annuncio come preferito
 */
@Serializable
data class Heart(
    val usernameUtente: String,
    val idAnnuncio: String
){}
