package com.example.clientnotesharing

import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.CambioPasswordRequest
import com.example.clientnotesharing.data.DatoDigitale
import com.example.clientnotesharing.data.Heart
import com.example.clientnotesharing.data.MaterialeDigitale
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.data.Persona
import com.example.clientnotesharing.data.UserSession
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

// Indirizzo IP del server
const val BASE_URL =  "http://192.168.43.216:8080" // "http://192.168.43.216:8080" // Per l'emulatore: "http://10.0.2.2:8080"

// Creazione dell'oggetto Retrofit, connettendosi al server
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json; charset=UTF8".toMediaType()))
    .build()

// 'E lazy in modo da avere un unuca istanza (singleton)
object NotesApi{
    val retrofitService : NoteSharingApi by lazy {
        retrofit.create(NoteSharingApi::class.java) //è l'inizializzazione di retrofitService
    }
    // Chiamando NotesApi.retrofitService, si prende l'unica istanza dell'oggetto Retrofit
}

// Interfaccia per conoscere i metodi offerti dal server
interface NoteSharingApi{
    // notesRoute
    // Metodo che permette al client di inviare un PDF
    @POST("uploadPdf")
    suspend fun uploadPdf(@Body datoD: DatoDigitale): Response<MessageResponse>
    // Metodo che permette al client di ricevere i PDF associati ad un annuncio
    @GET("getPDFs")
    suspend fun getPDFs(@Query("idAnnuncio") idAnnuncio: String): Response<ArrayList<DatoDigitale>>
    // Metodo che permette al client di inviare un annuncio al server
    @POST("uploadAnnuncio")
    suspend fun uploadAnnuncio(@Body annuncio: Annuncio): Response<MessageResponse>
    // Metodo che permette al client di inviare il contenuto di un annuncio di tipo digitale al server
    @POST("uploadMD")
    suspend fun uploadMaterialeDigitale(@Body annuncio: MaterialeDigitale): Response<MessageResponse>
    // Metodo che permette al client di inviare il contenuto di un annuncio di tipo fisico al server
    @POST("uploadMF")
    suspend fun uploadMaterialeFisico(@Body annuncio: MaterialeFisico): Response<MessageResponse>
    // Metodo che permette al client di salvare un annuncio come preferito (aggiornando il campo preferito)
    @POST("salvaAnnuncioComePreferito")
    suspend fun salvaAnnuncioComePreferito(@Body heart: Heart): Response<MessageResponse>
    // Metodo che permette al client di eliminare un annuncio come preferito (aggiornando il campo preferito)
    @POST("eliminaAnnuncioComePreferito")
    suspend fun eliminaAnnuncioComePreferito(@Body heart: Heart): Response<MessageResponse>
    // Metodo che restituisce gli annunci preferiti dell'utente
    @POST("getPrefetitiUtente")
    suspend fun getPreferitiUtente(@Body username: String): Response<ArrayList<Annuncio>>
    // Metodo che permette al client di eliminare un annuncio
    @POST("eliminaAnnuncio")
    suspend fun eliminaAnnuncio(@Body idAnnuncio: String): Response<MessageResponse>
    // Metodo che permette al client di ricevere tutti gli annunci tranne quelli pubblicati dall'utente stesso
    @GET("listaAnnunci")
    suspend fun getAnnunci(@Query("username") username: String): Response<ArrayList<Annuncio>>
    // Metodo che permette al client di ricevere tutti gli annunci creati da lui
    @GET("myAnnunci")
    suspend fun getMyAnnunci(@Query("username") username: String): Response<ArrayList<Annuncio>>

    // Metodo che permette al client di ricevere i dati del materiale fisico dell'annuncio preso in input
    @GET("materialeFisicoAssociatoAnnuncio")
    suspend fun getMaterialeFisicoAnnuncio(@Query("idAnnuncio") idAnnuncio: String): Response<MaterialeFisico>
    // Metodo che permette al client di ricevere i dati del materiale digitale dell'annuncio preso in input
    @GET("materialeDigitaleAssociatoAnnuncio")
    suspend fun getMaterialeDigitaleAnnuncio(@Query("idAnnuncio") idAnnuncio: String): Response<MaterialeDigitale>

    //personeRoute
    // Metodo che permette al client di fare un tentativo di login. Riceve come risposta se il login è stato fatto con successo
    @POST("UserLogin")
    suspend fun uploadLogin(@Body userSession: UserSession): Response<MessageResponse>
    // Metodo che permette al client di fare la registrazione
    @POST("UserSignUp")
    suspend fun uploadSignUp(@Body persona: Persona): Response<MessageResponse>
    // Metodo che permette al client di modificare la password
    @POST("CambioPassword")
    suspend fun cambioPsw(@Body dati: CambioPasswordRequest): Response<MessageResponse>
    // Metodo che restituisce la mail della persona con lo username di input
    @GET("MailFromUsername")
    suspend fun getMailFromUsername(@Query("username") username: String): Response<MessageResponse>
}

