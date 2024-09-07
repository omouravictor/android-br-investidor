package com.omouravictor.invest_view.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName

data class AssetGlobalQuoteItemResponse(
    @SerializedName("01. symbol") val symbol: String = "",
    @SerializedName("02. open") val open: Double = 0.0,
    @SerializedName("03. high") val high: Double = 0.0,
    @SerializedName("04. low") val low: Double = 0.0,
    @SerializedName("05. price") val price: Double = 0.0,
    @SerializedName("06. volume") val volume: Int = 0,
    @SerializedName("07. latest trading day") val latestTradingDay: String = "",
    @SerializedName("08. previous close") val previousClose: Double = 0.0,
    @SerializedName("09. change") val change: Double = 0.0,
    @SerializedName("10. change percent") val changePercent: String = ""
)