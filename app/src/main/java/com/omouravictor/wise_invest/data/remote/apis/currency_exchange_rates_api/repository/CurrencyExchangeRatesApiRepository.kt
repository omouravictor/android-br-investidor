package com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.repository

import com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.model.ConversionResultResponse

interface CurrencyExchangeRatesApiRepository {

    suspend fun convert(
        fromCurrency: String,
        toCurrency: String,
        amount: Int = 1
    ): Result<ConversionResultResponse>

}