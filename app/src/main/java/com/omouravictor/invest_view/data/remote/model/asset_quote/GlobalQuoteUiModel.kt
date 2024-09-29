package com.omouravictor.invest_view.data.remote.model.asset_quote

data class GlobalQuoteUiModel(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val price: Double,
    val volume: Int,
    val latestTradingDay: String,
    val previousClose: Double,
    val change: Double,
    val changePercent: Double
)
