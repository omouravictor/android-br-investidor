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
        price = globalQuoteResponse.price,
        change = globalQuoteResponse.change,
        changePercent = globalQuoteResponse.changePercent.getDoubleValue()
    )
}