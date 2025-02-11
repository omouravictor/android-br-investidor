package com.omouravictor.wise_invest.data.remote.apis.free_currency_api.model

import com.omouravictor.wise_invest.presenter.wallet.model.CurrencyExchangeRateUiModel
import com.omouravictor.wise_invest.util.getRoundedDouble

data class CurrencyExchangeRateResponse(
    val data: Map<String, Double>
)

fun CurrencyExchangeRateResponse.toCurrencyExchangeRateUiModel(): CurrencyExchangeRateUiModel {
    return CurrencyExchangeRateUiModel(
        data = data.mapValues { it.value.getRoundedDouble() }
    )
}