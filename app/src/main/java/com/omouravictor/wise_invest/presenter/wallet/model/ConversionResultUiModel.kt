package com.omouravictor.wise_invest.presenter.wallet.model

import com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.model.InfoUiModel
import com.omouravictor.wise_invest.data.remote.apis.currency_exchange_rates_api.model.QueryResponse

data class ConversionResultUiModel(
    val date: String?,
    val info: InfoUiModel,
    val query: QueryResponse?,
    val result: Double,
    val success: Boolean?
)
