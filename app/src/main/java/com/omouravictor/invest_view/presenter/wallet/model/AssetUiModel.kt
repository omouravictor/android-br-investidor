package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForLongNumber

data class AssetUiModel(
    val symbol: String,
    val name: String,
    val assetType: AssetTypes,
    val region: String,
    val currency: String,
    val currencyResColor: Int,
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
)

fun AssetUiModel.getTotalAssetPrice() = price * amount

fun AssetUiModel.getFormattedAmount() = "(${getFormattedValueForLongNumber(amount)})"

fun AssetUiModel.getFormattedTotalAssetPrice() = getFormattedCurrencyValue(currency, getTotalAssetPrice())