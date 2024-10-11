package com.omouravictor.wise_invest.data.remote.model.currency_exchange_rate

import com.omouravictor.wise_invest.presenter.wallet.model.ConversionResultUiModel

data class ConversionResultResponse(
    val date: String?,
    val info: InfoResponse?,
    val query: QueryResponse?,
    val result: Double?,
    val success: Boolean?
)

fun ConversionResultResponse.toConversionResultUiModel(): ConversionResultUiModel {
    return ConversionResultUiModel(
        date = date,
        info = info?.toInfoUiModel() ?: InfoUiModel(0.0, 0),
        query = query,
        result = result ?: 0.0,
        success = success
    )
}