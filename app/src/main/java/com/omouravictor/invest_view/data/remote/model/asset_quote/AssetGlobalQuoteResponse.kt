package com.omouravictor.invest_view.data.remote.model.asset_quote

import com.google.gson.annotations.SerializedName

data class AssetGlobalQuoteResponse(
    @SerializedName("Global Quote") val globalQuote: AssetGlobalQuoteItemResponse
)