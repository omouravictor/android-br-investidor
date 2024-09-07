package com.omouravictor.invest_view.data.remote.api

import com.omouravictor.invest_view.data.remote.model.news.NewsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {

    @GET("v2/everything")
    suspend fun getNewsBySearch(
        @Query("q") keywords: String,
        @Query("page") page: Int = 1
    ): NewsResponse

}