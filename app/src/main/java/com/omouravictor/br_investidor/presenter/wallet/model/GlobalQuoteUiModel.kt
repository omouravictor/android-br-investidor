package com.omouravictor.br_investidor.presenter.wallet.model

data class GlobalQuoteUiModel(
    val symbol: String,
    val price: Double,
    val change: Double,
    val changePercent: Double
)
