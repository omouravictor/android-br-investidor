package com.omouravictor.invest_view.data.network.alpha_vantage

import com.omouravictor.invest_view.data.network.alpha_vantage.model.asset_search.AssetMatchesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {

    @GET("query?function=SYMBOL_SEARCH")
    suspend fun getAssetsBySearch(
        @Query("keywords") keywords: String
    ): AssetMatchesResponse

}