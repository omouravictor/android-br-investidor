package com.omouravictor.invest_view.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName
import com.omouravictor.invest_view.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.invest_view.util.getDoubleValue

data class AssetGlobalQuoteResponse(
    @SerializedName("Global Quote") val globalQuote: GlobalQuote
)

fun AssetGlobalQuoteResponse.toGlobalQuoteUiModel(): GlobalQuoteUiModel {
    return GlobalQuoteUiModel(
        symbol = globalQuote.symbol,
        open = globalQuote.open,
        high = globalQuote.high,
        low = globalQuote.low,
        price = globalQuote.price,
        volume = globalQuote.volume,
        latestTradingDay = globalQuote.latestTradingDay,
        previousClose = globalQuote.previousClose,
        change = globalQuote.change,
        changePercent = globalQuote.changePercent.getDoubleValue()
    )
}