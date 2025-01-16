package ro.pub.cs.systems.eim.practicaltest02v9

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface AnagramApi {
    @GET("all/:{word}")
    suspend fun getAnagrams(@Path("word") word: String): Response<AnagramResponse>
}

data class AnagramResponse(
    val all: List<String>
)

val retrofit = Retrofit.Builder()
    .baseUrl("http://www.anagramica.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

val api = retrofit.create(AnagramApi::class.java)