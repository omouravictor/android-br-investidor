package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.ConversionResultResponse

interface CurrencyExchangeRatesApiRepository {

    suspend fun convert(
        fromCurrency: String,
        toCurrency: String,
        amount: Int = 1
    ): Result<ConversionResultResponse>

}