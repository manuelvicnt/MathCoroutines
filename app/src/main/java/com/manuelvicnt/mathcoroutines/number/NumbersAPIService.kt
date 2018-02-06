package com.manuelvicnt.mathcoroutines.number

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.experimental.CoroutineCallAdapterFactory
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.launch
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import kotlin.coroutines.experimental.CoroutineContext

class NumbersApiService(private val numbersApi: NumbersApiInterface) {

    suspend fun getNumberFunFact(context: CoroutineContext, number: Long): String {
        var result = ""

        launch(context + CommonPool) {
            result = getFunFactResponse(number)
        }.join()

        return result
    }

    private suspend fun getFunFactResponse(number: Long): String =
            try {
                val response = numbersApi.getFunFact(number).await()
                if (response.isSuccessful) {
                    response.body()?.string() ?: ""
                } else {
                    ""
                }
            } catch (e: Exception) {
                ""
            }
}

object NumbersApiHelper {

    private val retrofit = Retrofit.Builder()
            .baseUrl("http://numbersapi.com")
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()

    val numbersApi: NumbersApiInterface
        get() = retrofit.create(NumbersApiInterface::class.java)
}

interface NumbersApiInterface {

    @GET("/{number}")
    fun getFunFact(@Path("number") number: Long): Deferred<Response<ResponseBody>>

}