package com.example.clientnotesharing.ui

/*
This file is the corresponding view model for the MarsPhotosApp.
This class contains a MutableState property named marsUiState. Updating the value of this property updates the placeholder text displayed on the screen.
The getMarsPhotos() method updates the placeholder response. Later in the codelab, you use this method to display the data fetched from the server. The goal for this codelab is to update the MutableState within the ViewModel using data you get from the internet.
 */


/*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewModelScope
import data.MaterialeFisico
import com.example.clientnotesharing.data.NotesApi

class NotesViewModel: ViewModel(){
    /** The mutable State that stores the status of the most recent request */
    var notesUiState: MaterialeFisico by mutableStateOf(MaterialeFisico()) //ha già i valori di default
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getNotes()
    }

    /**
     * Prende il materiale dal NotesAPI Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getNotes() {
        viewModelScope.launch {
            val listResult = NotesApi.retrofitService.getMaterialeFisico() //contiene il risultato restituito dal server
            notesUiState = listResult //CONTERRA IL RISULTATO RICEVUTO!!!!!!!!!!!!!*************
        } //launches the coroutine
    }
}

 */
