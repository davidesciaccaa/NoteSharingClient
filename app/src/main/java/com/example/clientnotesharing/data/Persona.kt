package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

/*
 * Data class per inviare/ricevere correttamente i dati di registrazione di un utente
 */
@Serializable
class Persona(
    var username: String,
    var email: String,
    var password: String,
    var cf: String,
    var nome: String,
    var cognome: String,
    var provincia: String,
    var comune: String,
    var via: String,
    var nrCivico: Int,
    var cap: Int,
    var dataN: String
){}
