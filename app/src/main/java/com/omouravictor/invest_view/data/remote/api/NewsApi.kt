package com.omouravictor.invest_view.data.remote.api

import com.omouravictor.invest_view.data.remote.model.news.NewsResponse
import com.omouravictor.invest_view.util.LocaleUtil
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApi {

    @GET("v2/everything")
    suspend fun getNewsBySearch(
        @Query("q") keywords: String,
        @Query("language") language: String = LocaleUtil.appLocale.language,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("pageSize") pageSize: Int = 50,
        @Query("page") page: Int = 1,
    ): NewsResponse

}