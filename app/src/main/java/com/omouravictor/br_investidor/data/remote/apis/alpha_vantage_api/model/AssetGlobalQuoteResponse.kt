package com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model

import com.google.gson.annotations.SerializedName
import com.omouravictor.br_investidor.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.br_investidor.util.getRoundedDouble

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

data class AssetGlobalQuoteResponse(
    @SerializedName("Global Quote") val globalQuoteResponse: GlobalQuoteResponse
)

fun AssetGlobalQuoteResponse.toGlobalQuoteUiModel(): GlobalQuoteUiModel {
    return GlobalQuoteUiModel(
        symbol = globalQuoteResponse.symbol,
        price = globalQuoteResponse.price.getRoundedDouble(),
        change = globalQuoteResponse.change.getRoundedDouble(),
        changePercent = globalQuoteResponse.changePercent.removeSuffix("%").toDoubleOrNull()?.getRoundedDouble() ?: 0.0
    )
}