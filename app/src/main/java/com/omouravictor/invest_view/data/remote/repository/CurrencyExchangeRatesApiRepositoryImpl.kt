package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.omouravictor.invest_view.data.remote.api.CurrencyExchangeRatesApi
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.CurrencyExchangeRatesResponse
import com.omouravictor.invest_view.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class CurrencyExchangeRatesApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val currencyExchangeRatesApi: CurrencyExchangeRatesApi
) : CurrencyExchangeRatesApiRepository {

    override suspend fun getCurrencyExchangeRates(
        fromCurrency: String,
        toCurrency: String,
        amount: Int
    ): Result<CurrencyExchangeRatesResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = currencyExchangeRatesApi.getCurrencyExchange(fromCurrency, toCurrency, amount)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetCurrencyExchangeRate", "From: $fromCurrency, To: $toCurrency", e)
                Result.failure(e)
            }
        }
    }

}