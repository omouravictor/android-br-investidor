package com.omouravictor.invest_view.data.local.entity

import com.omouravictor.invest_view.ui.rates.model.RateUiModel
import java.util.Date

data class RateEntity(
    val currencyTerm: String,
    val unitaryRate: Double,
    val variation: Double,
    val rateDate: Date
)

fun RateEntity.toRateUiModel() = RateUiModel(
    "StringUtils.getCurrencyNameInPortuguese(currencyTerm)",
    currencyTerm,
    unitaryRate,
    variation,
    rateDate
)