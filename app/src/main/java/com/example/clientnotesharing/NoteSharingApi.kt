package com.example.clientnotesharing

import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.DatoDigitale
import com.example.clientnotesharing.data.MaterialeDigitale
import retrofit2.Retrofit
import retrofit2.http.GET
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.example.clientnotesharing.data.MaterialeFisico
import com.example.clientnotesharing.data.MessageResponse
import com.example.clientnotesharing.data.Persona
import com.example.clientnotesharing.data.UserSession
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query


const val BASE_URL =  "http://192.168.203.90:8080" // "http://10.0.2.2:8080"  //
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json; charset=UTF8".toMediaType()))
    .build()
object NotesApi{
    val retrofitService : NoteSharingApi by lazy {
        retrofit.create(NoteSharingApi::class.java) //è l'inizializzazione di retrofitService
    }
}
interface NoteSharingApi{
    //notesRoute
    @POST("uploadPdf")
    suspend fun uploadPdf(@Body datoD: DatoDigitale): Response<MessageResponse>
    @GET("getPDFs")
    suspend fun getPDFs(@Query("idAnnuncio") idAnnuncio: String): Response<ArrayList<DatoDigitale>>
    @POST("uploadAnnuncio")
    suspend fun uploadAnnuncio(@Body annuncio: Annuncio): Response<MessageResponse>
    @POST("uploadMD")
    suspend fun uploadMaterialeDigitale(@Body annuncio: MaterialeDigitale): Response<MessageResponse>
    @POST("uploadMF")
    suspend fun uploadMaterialeFisico(@Body annuncio: MaterialeFisico): Response<MessageResponse>
    @POST("salvaAnnuncioComePreferito")
    suspend fun salvaAnnuncioComePreferito(@Body idAnnuncio: String): Response<MessageResponse>
    @POST("eliminaAnnuncioComePreferito")
    suspend fun eliminaAnnuncioComePreferito(@Body idAnnuncio: String): Response<MessageResponse>
    @POST("eliminaAnnuncio")
    suspend fun eliminaAnnuncio(@Body idAnnuncio: String): Response<MessageResponse>
    @GET("listaAnnunci")
    suspend fun getAnnunci(@Query("username") username: String): Response<ArrayList<Annuncio>> //restituisce tutti gli annunci tranne quelli pubblicati dall'utente.
    @GET("myAnnunci")
    suspend fun getMyAnnunci(@Query("username") username: String): Response<ArrayList<Annuncio>>
    @GET("listaAnnunciSalvati")
    suspend fun getAnnunciSalvati(@Query("username") username: String): Response<ArrayList<Annuncio>> //restituisce gli annunci salvati
    @GET("materialeFisicoAssociatoAnnuncio")
    suspend fun getMaterialeFisicoAnnuncio(@Query("idAnnuncio") idAnnuncio: String): Response<MaterialeFisico>
    @GET("materialeDigitaleAssociatoAnnuncio")
    suspend fun getMaterialeDigitaleAnnuncio(@Query("idAnnuncio") idAnnuncio: String): Response<MaterialeDigitale>
    //personeRoute
    @POST("UserLogin")
    suspend fun uploadLogin(@Body userSession: UserSession): Response<MessageResponse>
    @POST("UserSignUp")
    suspend fun uploadSignUp(@Body persona: Persona): Response<MessageResponse>

}

/*
The call to create() function on a Retrofit object is expensive in terms
of memory, speed, and performance. The app needs only one instance of
the Retrofit API service, so you expose the service to the rest of the
app using object declaration. Usando object sarà un singleton.
 */

/*
The Retrofit setup is done! Each time your app calls NotesApi.retrofitService,
the caller accesses the same singleton Retrofit object that implements
NotesSharingApi, which is created on the first access. In the next task,
you use the Retrofit object you implemented.
 */

