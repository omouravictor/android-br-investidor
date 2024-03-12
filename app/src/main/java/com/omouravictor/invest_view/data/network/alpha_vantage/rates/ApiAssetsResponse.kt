package com.omouravictor.invest_view.data.network.alpha_vantage.rates

import com.google.gson.annotations.SerializedName
import com.omouravictor.invest_view.presenter.wallet.assets.model.AssetUiModel
import java.util.*

data class ApiAssetsResponse(
    @SerializedName("results")
    val results: ApiAssetsResultResponse,

    var rateDate: Date
)

fun ApiAssetsResponse.toAssetsUiModelList(): List<AssetUiModel> {
    return results.currencies.map { (currenciesMapKey, currenciesMapValue) ->
        AssetUiModel(
            "StringUtils.getCurrencyNameInPortuguese(currenciesMapKey)",
            currenciesMapKey,
            currenciesMapValue.buy,
            currenciesMapValue.variation,
            rateDate
        )
    }
}