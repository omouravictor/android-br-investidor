package com.omouravictor.invest_view.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName
import com.omouravictor.invest_view.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.invest_view.util.getDoubleValue

data class AssetGlobalQuoteResponse(
    @SerializedName("Global Quote") val globalQuoteResponse: GlobalQuoteResponse
)

fun AssetGlobalQuoteResponse.toGlobalQuoteUiModel(): GlobalQuoteUiModel {
    return GlobalQuoteUiModel(
        symbol = globalQuoteResponse.symbol,
        open = globalQuoteResponse.open,
        high = globalQuoteResponse.high,
        low = globalQuoteResponse.low,
        price = globalQuoteResponse.price,
        volume = globalQuoteResponse.volume,
        latestTradingDay = globalQuoteResponse.latestTradingDay,
        previousClose = globalQuoteResponse.previousClose,
        change = globalQuoteResponse.change,
        changePercent = globalQuoteResponse.changePercent.getDoubleValue()
    )
}