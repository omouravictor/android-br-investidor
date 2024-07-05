package com.omouravictor.invest_view.util

import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes

object AssetUtil {

    fun getAssetType(symbol: String, type: String): AssetTypes {
        return when (type) {
            "Equity" -> {
                if (symbol.substringAfter(".") == "SAO") {
                    val formattedSymbol = getFormattedSymbol(symbol)
                    val lastTwoDigits = formattedSymbol.takeLast(2)
                    if (lastTwoDigits == "34" || lastTwoDigits == "35" || lastTwoDigits == "32" || lastTwoDigits == "33")
                        AssetTypes.BDR
                    else
                        AssetTypes.BRAZILIAN_STOCK
                } else {
                    AssetTypes.STOCK
                }
            }

            "Mutual Fund" -> AssetTypes.FI
            "ETF" -> AssetTypes.ETF
            else -> AssetTypes.OTHER
        }
    }

    fun getCurrencyResColor(currency: String): Int {
        return when (currency) {
            "USD" -> R.color.usd
            "BRL" -> R.color.brl
            "EUR" -> R.color.eur
            "CAD" -> R.color.cad
            "INR" -> R.color.inr
            "CNY" -> R.color.cny
            else -> R.color.other
        }
    }

    fun getFormattedSymbol(symbol: String) = symbol.substringBefore(".")

}