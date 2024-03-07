package com.omouravictor.invest_view.data.network.hgfinanceapi.rates

import com.google.gson.annotations.SerializedName

data class ApiAssetsResultResponse(
    @SerializedName("currencies")
    val currencies: LinkedHashMap<String, ApiAssetsResultItemResponse>
)
