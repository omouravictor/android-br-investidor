package com.omouravictor.br_investidor.data.remote.apis.news_api.repository

import com.omouravictor.br_investidor.data.remote.apis.news_api.model.news.NewsResponse

interface NewsApiRepository {
    suspend fun getNewsBySearch(keywords: String): Result<NewsResponse>
}