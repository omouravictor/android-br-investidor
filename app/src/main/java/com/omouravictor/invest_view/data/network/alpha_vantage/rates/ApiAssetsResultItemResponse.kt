package com.omouravictor.invest_view.data.network.alpha_vantage.rates

import com.google.gson.annotations.SerializedName

data class ApiAssetsResultItemResponse(
    @SerializedName("buy")
    val buy: Double,

    @SerializedName("variation")
    val variation: Double
)
