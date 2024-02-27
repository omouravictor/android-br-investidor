package com.omouravictor.invest_view.data.network.hgfinanceapi.rates

import com.google.gson.annotations.SerializedName
import com.omouravictor.invest_view.ui.rates.model.RateUiModel
import com.omouravictor.invest_view.data.local.entity.RateEntity
import java.util.*

data class ApiRatesResponse(
    @SerializedName("results")
    val results: ApiRatesResultsResponse,

    var rateDate: Date
)

fun ApiRatesResponse.toRatesEntityList(): List<RateEntity> {
    return results.currencies.map { (currenciesMapKey, currenciesMapValue) ->
        RateEntity(
            currenciesMapKey,
            currenciesMapValue.buy,
            currenciesMapValue.variation,
            rateDate
        )
    }
}

fun ApiRatesResponse.toRatesUiModelList(): List<RateUiModel> {
    return results.currencies.map { (currenciesMapKey, currenciesMapValue) ->
        RateUiModel(
            "StringUtils.getCurrencyNameInPortuguese(currenciesMapKey)",
            currenciesMapKey,
            currenciesMapValue.buy,
            currenciesMapValue.variation,
            rateDate
        )
    }
}