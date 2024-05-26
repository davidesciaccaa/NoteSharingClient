package com.example.clientnotesharing

import com.example.clientnotesharing.data.Annuncio
import com.example.clientnotesharing.data.MaterialeDigitale
import retrofit2.Retrofit
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


const val BASE_URL = "http://10.0.2.2:8080"
private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(
        Json.asConverterFactory(
            "application/json; charset=UTF8".toMediaType()))
    .build()
object NotesApi{
    val retrofitService : NoteSharingApi by lazy {
        retrofit.create(NoteSharingApi::class.java) //retrofitService initialization
    }
    /*
    You make this lazy initialization to make sure it is initialized at its first usage.
    Ignore the error, which you fix in the next steps.
     */
}
interface NoteSharingApi{
    @Multipart
    @POST("uploadPdf")
    suspend fun uploadPdf(@Part file: MultipartBody.Part): ResponseBody

    @POST("uploadAnnuncio")
    suspend fun uploadAnnuncio(@Body annuncio: Annuncio)

    @POST("uploadMD")
    suspend fun uploadMaterialeDigitale(@Body annuncio: MaterialeDigitale)

    @POST("uploadMF")
    suspend fun uploadMaterialeFisico(@Body annuncio: MaterialeFisico)

    @POST("UserLogin")
    suspend fun uploadLogin(@Body userSession: UserSession): Response<MessageResponse>

    @POST("UserSignUp")
    suspend fun uploadSignUp(@Body persona: Persona)
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

