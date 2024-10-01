package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.InfoUiModel
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.QueryResponse

data class ConversionResultUiModel(
    val date: String?,
    val info: InfoUiModel,
    val query: QueryResponse?,
    val result: Double,
    val success: Boolean?
)
