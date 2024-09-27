package com.omouravictor.invest_view.data.remote.api

import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.AssetsBySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {

    @GET("query?function=SYMBOL_SEARCH")
    suspend fun getAssetsBySearch(
        @Query("keywords") keywords: String
    ): AssetsBySearchResponse

    @GET("query?function=GLOBAL_QUOTE")
    suspend fun getAssetGlobalQuote(
        @Query("symbol") symbol: String
    ): AssetGlobalQuoteResponse

}