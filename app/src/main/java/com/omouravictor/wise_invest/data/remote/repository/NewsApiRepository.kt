package com.omouravictor.wise_invest.data.remote.repository

import com.omouravictor.wise_invest.data.remote.model.news.NewsResponse

interface NewsApiRepository {
    suspend fun getNewsBySearch(keywords: String): Result<NewsResponse>
}