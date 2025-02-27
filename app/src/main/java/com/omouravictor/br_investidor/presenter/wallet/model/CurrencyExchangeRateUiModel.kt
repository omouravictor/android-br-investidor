package com.omouravictor.br_investidor.presenter.wallet.model

import com.omouravictor.br_investidor.util.LocaleUtil

data class CurrencyExchangeRateUiModel(
    val data: Map<String, Double>
)

fun CurrencyExchangeRateUiModel.getRateForAppCurrency(): Double? = data[LocaleUtil.appCurrency.toString()]