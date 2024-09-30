package com.omouravictor.invest_view.data.remote.model.currency_exchange_rate

import com.omouravictor.invest_view.presenter.wallet.model.ConversionResultUiModel

data class ConversionResultResponse(
    val date: String? = null,
    val info: InfoResponse? = null,
    val query: QueryResponse? = null,
    val result: Double? = null,
    val success: Boolean? = null
)

fun ConversionResultResponse.toConversionResultUiModel(): ConversionResultUiModel {
    return ConversionResultUiModel(
        date = date,
        info = info,
        query = query,
        result = result,
        success = success
    )
}