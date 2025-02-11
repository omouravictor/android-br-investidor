package com.omouravictor.wise_invest.data.remote.apis.free_currency_api.repository

import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.model.CurrencyExchangeRateResponse
import retrofit2.http.Query

interface CurrencyExchangeRatesApiRepository {

    suspend fun getLatestRate(
        @Query("base_currency") baseCurrency: String,
        @Query("currencies") currencies: String
    ): Result<CurrencyExchangeRateResponse>

}