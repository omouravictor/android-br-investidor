package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.LocaleUtil

data class AssetUiModel(
    val symbol: String,
    val name: String,
    val assetType: AssetTypes,
    val region: String,
    val currency: String,
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
)

fun AssetUiModel.getTotalAssetPrice() = price * amount

fun AssetUiModel.getFormattedSymbolAndAmount() =
    "${getFormattedSymbol()} (${LocaleUtil.getFormattedValueForLongNumber(amount)})"

fun AssetUiModel.getFormattedSymbol() = AssetUtil.getFormattedSymbol(symbol)

fun AssetUiModel.getFormattedTotalAssetPrice() = LocaleUtil.getFormattedValueForCurrency(currency, getTotalAssetPrice())