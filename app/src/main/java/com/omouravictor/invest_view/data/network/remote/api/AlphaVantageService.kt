package com.omouravictor.invest_view.data.network.remote.api

import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageService {

    @GET("query?function=SYMBOL_SEARCH&datatype=json")
    suspend fun getAssetsBySearch(
        @Query("keywords") keywords: String
    ): AssetsBySearchResponse

    @GET("query?function=GLOBAL_QUOTE&datatype=json")
    suspend fun getAssetGlobalQuote(
        @Query("symbol") symbol: String
    ): AssetGlobalQuoteResponse

}