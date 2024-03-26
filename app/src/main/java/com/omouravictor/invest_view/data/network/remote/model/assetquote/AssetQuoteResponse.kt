package com.omouravictor.invest_view.data.network.remote.model.assetquote

import com.google.gson.annotations.SerializedName
import com.omouravictor.invest_view.presenter.wallet.asset_quote.model.AssetQuoteUiModel

data class AssetQuoteResponse(
    @SerializedName("Global Quote") val globalQuote: GlobalQuoteResponse
)

fun AssetQuoteResponse.toAssetQuoteUiModel(): AssetQuoteUiModel {
    return AssetQuoteUiModel(
        symbol = globalQuote.symbol,
        open = globalQuote.open,
        high = globalQuote.high,
        low = globalQuote.low,
        price = globalQuote.price,
        volume = globalQuote.volume,
        latestTradingDay = globalQuote.latestTradingDay,
        previousClose = globalQuote.previousClose,
        change = globalQuote.change,
        changePercent = globalQuote.changePercent
    )
}