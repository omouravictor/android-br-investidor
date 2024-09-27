package com.omouravictor.invest_view.data.remote.model.currency_exchange_rate

import com.google.gson.annotations.SerializedName

data class CurrencyExchangeRateResponse(
    @SerializedName("Realtime Currency Exchange Rate")
    val currencyExchangeRate: CurrencyExchangeRateItemResponse
)