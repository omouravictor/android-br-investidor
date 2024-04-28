package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.util.LocaleUtil

data class AssetUiModel(
    val symbol: String,
    val name: String,
    val assetType: AssetTypes,
    val region: String,
    val currency: String,
    var price: Float = 0f,
    var amount: Long = 0,
    var totalInvested: Float = 0f
)

fun AssetUiModel.getTotalAssetPrice() = price * amount

fun AssetUiModel.getFormattedAmount() = "(${LocaleUtil.getFormattedValueForLongNumber(amount)})"

fun AssetUiModel.getFormattedTotalAssetPrice() = LocaleUtil.getFormattedValueForCurrency(currency, getTotalAssetPrice())