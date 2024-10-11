package com.omouravictor.wise_invest.presenter.wallet.model

import com.omouravictor.wise_invest.data.remote.model.currency_exchange_rate.InfoUiModel
import com.omouravictor.wise_invest.data.remote.model.currency_exchange_rate.QueryResponse

data class ConversionResultUiModel(
    val date: String?,
    val info: InfoUiModel,
    val query: QueryResponse?,
    val result: Double,
    val success: Boolean?
)
