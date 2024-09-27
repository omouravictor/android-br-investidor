package com.omouravictor.invest_view.data.remote.api

import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.ConversionResultResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyExchangeRatesApi {

    @GET("convert")
    suspend fun convert(
        @Query("from") fromCurrency: String,
        @Query("to") toCurrency: String,
        @Query("amount") amount: Int = 1
    ): ConversionResultResponse

}