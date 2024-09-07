package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.data.remote.model.news.NewsResponse

interface NewsApiRepository {
    suspend fun getNewsBySearch(keywords: String): Result<NewsResponse>
}