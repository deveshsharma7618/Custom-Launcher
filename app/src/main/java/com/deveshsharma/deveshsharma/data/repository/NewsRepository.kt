
package com.deveshsharma.deveshsharma.data.repository

import com.deveshsharma.deveshsharma.data.model.NewsResponse
import com.deveshsharma.deveshsharma.data.network.RetrofitInstance

class NewsRepository {

    private val newsApiService = RetrofitInstance.api

    suspend fun getNews(page: String? = null): NewsResponse {
        return newsApiService.getNews(page = page)
    }
}
