package com.omouravictor.wise_invest.data.remote.apis.free_currency_api.repository

import android.util.Log
import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.CurrencyExchangeRatesApi
import com.omouravictor.wise_invest.data.remote.apis.free_currency_api.model.CurrencyExchangeRateResponse
import com.omouravictor.wise_invest.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class CurrencyExchangeRatesApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val currencyExchangeRatesApi: CurrencyExchangeRatesApi
) : CurrencyExchangeRatesApiRepository {

    override suspend fun getLatestRate(
        baseCurrency: String,
        currencies: String
    ): Result<CurrencyExchangeRateResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = currencyExchangeRatesApi.getLatestRate(baseCurrency, currencies)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetCurrencyExchangeRate", "From: $baseCurrency, To: $currencies", e)
                Result.failure(e)
            }
        }
    }

}