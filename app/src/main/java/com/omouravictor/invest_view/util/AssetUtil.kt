package com.omouravictor.invest_view.util

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes

object AssetUtil {

    fun getAssetType(type: String): AssetTypes {
        val assetType = type.replace(" ", "_").uppercase()
        return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
    }

    fun getFormattedSymbol(symbol: String): String {
        return symbol.substringBeforeLast(".")
    }

    fun getVariation(totalAssetPrice: Double, totalInvested: Double): Double {
        return NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
    }

    fun getFormattedVariationAndPercent(currency: String, totalAssetPrice: Double, totalInvested: Double): String {
        val variation = getVariation(totalAssetPrice, totalInvested)
        val variationFormatted = LocaleUtil.getFormattedValueForCurrency(currency, variation)
        val percentFormatted = LocaleUtil.getFormattedValueForPercent(variation / totalInvested)
        return "$variationFormatted ($percentFormatted)"
    }

}