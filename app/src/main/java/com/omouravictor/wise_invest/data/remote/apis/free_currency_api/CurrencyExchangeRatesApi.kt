package com.omouravictor.wise_invest.data.remote.apis.free_currency_api

import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.model.CurrencyExchangeRateResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyExchangeRatesApi {

    @GET("latest")
    suspend fun getLatestRate(
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): CurrencyExchangeRateResponse

}