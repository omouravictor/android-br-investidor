package com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.model

data class QueryResponse(
    val amount: Int,
    val from: String,
    val to: String
)