package com.omouravictor.invest_view.data.remote.model.currency_exchange_rate

data class ConversionResultResponse(
    val date: String,
    val info: Info,
    val query: Query,
    val result: Double,
    val success: Boolean
)