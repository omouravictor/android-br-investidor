package com.omouravictor.br_investidor.data.remote.apis.free_currency_api.model

import com.omouravictor.br_investidor.presenter.wallet.model.CurrencyExchangeRateUiModel
import com.omouravictor.br_investidor.util.getRoundedDouble

data class CurrencyExchangeRateResponse(
    val data: Map<String, Double>
)

fun CurrencyExchangeRateResponse.toCurrencyExchangeRateUiModel(): CurrencyExchangeRateUiModel {
    return CurrencyExchangeRateUiModel(
        data = data.mapValues { it.value.getRoundedDouble() }
    )
}