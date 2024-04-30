package com.example.clientnotesharing.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/*
serve per il jetpak compose, per fare "var notesUiState: MaterialeFisico by mutableStateOf("")" in NotesViewModel
 */
/*
class NotesViewModel: ViewModel() {
    var materialeFisicoState by mutableStateOf(MaterialeFisico())

    // Getter method
    fun getMaterialeFisico(): MaterialeFisico {
        return materialeFisicoState
    }

    // Setter method
    fun setMaterialeFisico(materialeFisico: MaterialeFisico) {
        materialeFisicoState = materialeFisico
    }
}
 */


/*
i valori di default sono neccesari
 */
class MaterialeFisico(
    var costo: Int = 0,
    var annoRiferimento: Int = 0, //TROVARE UN GIOVO VAL DI DEFAULT
    var nomeCorso: String = "",
    var descrizioneMateriale: String = "",
    var comune: String = "",
    var provincia: String = "",
    var via: String = "",
    var cap: Int = 0
) {
    //in kotlin ci sono in automatico i getters e setters
}