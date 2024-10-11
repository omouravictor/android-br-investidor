package com.omouravictor.wise_invest.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName
import com.omouravictor.wise_invest.presenter.wallet.model.GlobalQuoteUiModel
import com.omouravictor.wise_invest.util.getRoundedDouble

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