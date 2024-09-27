package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.CurrencyExchangeRatesResponse

interface CurrencyExchangeRatesApiRepository {

    suspend fun getCurrencyExchangeRates(
        fromCurrency: String,
        toCurrency: String,
        amount: Int = 1
    ): Result<CurrencyExchangeRatesResponse>

}