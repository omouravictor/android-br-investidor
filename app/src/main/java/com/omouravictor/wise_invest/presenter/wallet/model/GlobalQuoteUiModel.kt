package com.omouravictor.wise_invest.presenter.wallet.model

data class GlobalQuoteUiModel(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double
)
