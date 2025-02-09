package com.omouravictor.wise_invest.data.remote.apis.news_api.model.news

data class ArticleResponse(
    val author: String?,
    val content: String?,
    val description: String?,
    val publishedAt: String?,
    val source: SourceResponse,
    val title: String?,
    val url: String,
    val urlToImage: String?
)