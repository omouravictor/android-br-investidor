package com.omouravictor.invest_view.data.remote.api

import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.CurrencyExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface AlphaVantageApi {

    @GET("query?function=SYMBOL_SEARCH&datatype=json")
    suspend fun getAssetsBySearch(
        @Query("keywords") keywords: String
    ): AssetsBySearchResponse

    @GET("query?function=GLOBAL_QUOTE&datatype=json")
    suspend fun getAssetGlobalQuote(
        @Query("symbol") symbol: String
    ): AssetGlobalQuoteResponse

    @GET("query?function=CURRENCY_EXCHANGE_RATE&datatype=json")
    suspend fun getCurrencyExchange(
        @Query("from_currency") fromCurrency: String,
        @Query("to_currency") toCurrency: String
    ): CurrencyExchangeRateResponse

}