package com.omouravictor.wise_invest.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName

data class GlobalQuoteResponse(
    @SerializedName("01. symbol") val symbol: String = "",
    @SerializedName("02. open") val open: Double = -1.0,
    @SerializedName("03. high") val high: Double = -1.0,
    @SerializedName("04. low") val low: Double = -1.0,
    @SerializedName("05. price") val price: Double = -1.0,
    @SerializedName("06. volume") val volume: Int = -1,
    @SerializedName("07. latest trading day") val latestTradingDay: String = "",
    @SerializedName("08. previous close") val previousClose: Double = -1.0,
    @SerializedName("09. change") val change: Double = -1.0,
    @SerializedName("10. change percent") val changePercent: String = ""
)