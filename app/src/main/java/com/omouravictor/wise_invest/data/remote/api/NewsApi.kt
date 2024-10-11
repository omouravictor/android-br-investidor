package com.omouravictor.wise_invest.data.remote.api

import com.omouravictor.wise_invest.data.remote.model.news.NewsResponse
import com.omouravictor.wise_invest.util.LocaleUtil
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