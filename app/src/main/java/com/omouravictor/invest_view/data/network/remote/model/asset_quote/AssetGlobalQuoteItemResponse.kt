package com.omouravictor.invest_view.data.network.remote.model.asset_quote

import com.google.gson.annotations.SerializedName

data class AssetGlobalQuoteItemResponse(
    @SerializedName("01. symbol") val symbol: String,
    @SerializedName("02. open") val open: Double,
    @SerializedName("03. high") val high: Double,
    @SerializedName("04. low") val low: Double,
    @SerializedName("05. price") val price: Double,
    @SerializedName("06. volume") val volume: Int,
    @SerializedName("07. latest trading day") val latestTradingDay: String,
    @SerializedName("08. previous close") val previousClose: Double,
    @SerializedName("09. change") val change: Double,
    @SerializedName("10. change percent") val changePercent: String
)