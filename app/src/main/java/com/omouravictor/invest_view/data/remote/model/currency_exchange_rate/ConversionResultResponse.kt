package com.omouravictor.invest_view.data.remote.model.currency_exchange_rate

data class ConversionResultResponse(
    val date: String? = null,
    val info: InfoResponse? = null,
    val query: QueryResponse? = null,
    val result: Double? = null,
    val success: Boolean? = null
)