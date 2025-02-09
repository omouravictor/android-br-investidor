package com.omouravictor.wise_invest.data.remote.apis.news_api.repository

import android.util.Log
import com.omouravictor.wise_invest.data.remote.apis.news_api.NewsApi
import com.omouravictor.wise_invest.data.remote.apis.news_api.model.news.NewsResponse
import com.omouravictor.wise_invest.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class NewsApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val newsApi: NewsApi
) : NewsApiRepository {

    override suspend fun getNewsBySearch(keywords: String): Result<NewsResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = newsApi.getNewsBySearch(keywords)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetNewsBySearch", "Keywords: $keywords", e)
                Result.failure(e)
            }
        }
    }

}