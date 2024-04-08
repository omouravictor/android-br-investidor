package com.omouravictor.invest_view.presenter.wallet.model

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.util.NumberUtil

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

fun AssetUiModel.getAssetType(): AssetTypes {
    val assetType = type.replace(" ", "_").uppercase()
    return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
}

fun AssetUiModel.getTotalAssetPrice() = price * amount

fun AssetUiModel.getVariation(): Pair<Double, Double> {
    val variation = NumberUtil.getRoundedDouble(getTotalAssetPrice() - totalInvested)
    val percent = variation / totalInvested
    return Pair(variation, percent)
}