package com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api

import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.assets_by_search.AssetsBySearchResponse
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