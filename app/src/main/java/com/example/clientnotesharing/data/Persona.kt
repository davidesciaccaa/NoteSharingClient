package com.example.clientnotesharing.data

import kotlinx.serialization.Serializable

@Serializable
class Persona(
    var vausername: String,
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