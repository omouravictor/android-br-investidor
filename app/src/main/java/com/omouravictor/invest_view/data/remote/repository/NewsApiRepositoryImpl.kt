package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.omouravictor.invest_view.data.remote.api.NewsService
import com.omouravictor.invest_view.data.remote.model.news.NewsResponse
import com.omouravictor.invest_view.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class NewsApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val newsService: NewsService
) : NewsApiRepository {

    override suspend fun getNewsBySearch(keywords: String): Result<NewsResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = newsService.getNewsBySearch(keywords)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetNewsBySearch", "Keywords $keywords", e)
                Result.failure(e)
            }
        }
    }

}