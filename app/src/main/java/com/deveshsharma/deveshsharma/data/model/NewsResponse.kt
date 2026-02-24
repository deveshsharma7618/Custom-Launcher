
package com.deveshsharma.deveshsharma.data.model

data class NewsResponse(
    val results: List<NewsArticle>,
    val nextPage: String?
)
