package com.omouravictor.invest_view.data.network.remote.api

import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {

    @GET("query?function=SYMBOL_SEARCH")
    suspend fun getAssetsBySearch(
        @Query("keywords") keywords: String
    ): AssetsBySearchResponse

}