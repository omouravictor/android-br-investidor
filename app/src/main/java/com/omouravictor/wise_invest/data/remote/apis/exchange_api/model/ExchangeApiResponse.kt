package com.omouravictor.wise_invest.data.remote.apis.exchange_api.model

data class ExchangeApiResponse(
    val date: String,
    val rates: RatesResponse
)