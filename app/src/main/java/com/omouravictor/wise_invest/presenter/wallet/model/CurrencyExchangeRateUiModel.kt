package com.omouravictor.wise_invest.presenter.wallet.model

import com.omouravictor.wise_invest.util.LocaleUtil

data class CurrencyExchangeRateUiModel(
    val data: Map<String, Double>
)

fun CurrencyExchangeRateUiModel.getRateForAppCurrency(): Double? = data[LocaleUtil.appCurrency.toString()]