package com.example.clientnotesharing.data
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET

const val BASE_URL = "http://10.0.2.2:8080" //del server ovviamente
private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()
object NotesApi{
    val retrofitService : NoteSharingApi by lazy {
        retrofit.create(NoteSharingApi::class.java) //è l'inizializzazione di retrofitService
    }
    /*
    You make this lazy initialization to make sure it is initialized at its first usage. Ignore the error, which you fix in the next steps.
     */
}
interface NoteSharingApi{
    @GET("materiale") //specifico landpoint nel server, senza il /???
    suspend fun getMaterialeFisico(): MaterialeFisico //penso deve essere implementato nel server oppure è la func da usare qua dentro nel client
    //essndo suspend diventa asincrono e non blocca il thread chiamante
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