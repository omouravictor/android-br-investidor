package com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api

import com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.model.ConversionResultResponse
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