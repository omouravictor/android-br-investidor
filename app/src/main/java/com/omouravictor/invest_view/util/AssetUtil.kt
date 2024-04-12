package com.omouravictor.invest_view.util

object AssetUtil {

    fun getDisplaySymbol(symbol: String) = symbol.substringBeforeLast(".")

    fun getVariation(totalAssetPrice: Double, totalInvested: Double): Pair<Double, Double> {
        val variation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val percent = variation / totalInvested
        return Pair(variation, percent)
    }

}