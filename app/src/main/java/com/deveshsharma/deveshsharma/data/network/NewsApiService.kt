
package com.deveshsharma.deveshsharma.data.network

import com.deveshsharma.deveshsharma.data.model.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    @GET("latest")
    suspend fun getNews(
        @Query("apikey") apiKey: String = "pub_527924a397b140d7a28a7f191550a78c",
        @Query("q") query: String = "coding,technology",
        @Query("page") page: String? = null
    ): NewsResponse
}
