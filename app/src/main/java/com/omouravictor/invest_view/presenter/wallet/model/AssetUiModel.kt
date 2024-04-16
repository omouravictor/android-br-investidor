package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.LocaleUtil

data class AssetUiModel(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val currency: String,
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
)

fun AssetUiModel.getTotalAssetPrice() = price * amount

fun AssetUiModel.getFormattedTotalAssetPrice() = LocaleUtil.getFormattedValueForCurrency(currency, getTotalAssetPrice())

fun AssetUiModel.getAssetType(): AssetTypes {
    val assetType = type.replace(" ", "_").uppercase()
    return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
}

fun AssetUiModel.getFormattedVariation(): String {
    val (variation, percent) = AssetUtil.getVariation(getTotalAssetPrice(), totalInvested)
    val variationCurrency = LocaleUtil.getFormattedValueForCurrency(currency, variation)
    val percentFormatted = LocaleUtil.getFormattedValueForPercent(percent)
    return "$variationCurrency ($percentFormatted)"
}