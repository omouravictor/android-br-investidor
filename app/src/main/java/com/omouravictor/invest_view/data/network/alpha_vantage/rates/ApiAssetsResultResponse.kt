package com.omouravictor.invest_view.data.network.alpha_vantage.rates

import com.google.gson.annotations.SerializedName

data class ApiAssetsResultResponse(
    @SerializedName("currencies")
    val currencies: LinkedHashMap<String, ApiAssetsResultItemResponse>
)
