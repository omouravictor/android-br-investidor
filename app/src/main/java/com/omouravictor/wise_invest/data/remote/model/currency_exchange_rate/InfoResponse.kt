package com.omouravictor.wise_invest.data.remote.model.currency_exchange_rate

import com.omouravictor.wise_invest.util.getRoundedDouble

data class InfoResponse(
    val rate: Double?,
    val timestamp: Int?
)

data class InfoUiModel(
    val rate: Double,
    val timestamp: Int
)

fun InfoResponse.toInfoUiModel(): InfoUiModel {
    return InfoUiModel(
        rate = rate?.getRoundedDouble() ?: 0.0,
        timestamp = timestamp ?: 0
    )
}