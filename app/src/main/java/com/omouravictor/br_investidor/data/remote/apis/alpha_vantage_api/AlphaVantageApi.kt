package com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api

import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetGlobalQuoteResponse
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetsBySearchResponse
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