package com.omouravictor.br_investidor.data.remote.apis.news_api

import com.omouravictor.br_investidor.data.remote.apis.news_api.model.news.NewsResponse
import com.omouravictor.br_investidor.util.LocaleUtil
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