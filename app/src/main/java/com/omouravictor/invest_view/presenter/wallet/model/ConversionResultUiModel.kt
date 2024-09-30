package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.InfoResponse
import com.omouravictor.invest_view.data.remote.model.currency_exchange_rate.QueryResponse

data class ConversionResultUiModel(
    val date: String? = null,
    val info: InfoResponse? = null,
    val query: QueryResponse? = null,
    val result: Double? = null,
    val success: Boolean? = null
)
