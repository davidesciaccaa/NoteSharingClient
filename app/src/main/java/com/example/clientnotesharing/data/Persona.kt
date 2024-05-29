package com.example.clientnotesharing.data
import kotlinx.serialization.Serializable

@Serializable
data class Persona(
    val username: String,
    val email: String,
    val password: String,
    val cf: String,
    val nome: String,
    val cognome: String,
    val provincia: String,
    val comune: String,
    val via: String,
    val nrCivico: Int,
    val cap: Int,
    val dataN: String //la data è string perchè nessuno dei tipi di DATE supportano la serializzazione
)
