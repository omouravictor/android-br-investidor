package com.omouravictor.br_investidor.util

import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.presenter.wallet.asset_types.AssetType

object AssetUtil {

    fun getAssetType(symbol: String, type: String): AssetType {
        return when (type) {
            "Equity" -> {
                if (symbol.substringAfter(".") == "SAO") {
                    val formattedSymbol = getFormattedSymbol(symbol)
                    val lastTwoDigits = formattedSymbol.takeLast(2)
                    if (lastTwoDigits == "34" || lastTwoDigits == "35" || lastTwoDigits == "32" || lastTwoDigits == "33")
                        AssetType.BDR
                    else
                        AssetType.BRAZILIAN_STOCK
                } else {
                    AssetType.STOCK
                }
            }

            "Mutual Fund" -> AssetType.FI
            "ETF" -> AssetType.ETF
            else -> AssetType.DEFAULT
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
            "GBX" -> R.color.gbx
            else -> R.color.other
        }
    }

    fun getCurrencyResName(currency: String): Int? {
        return when (currency) {
            "USD" -> R.string.usd
            "BRL" -> R.string.brl
            "EUR" -> R.string.eur
            "CAD" -> R.string.cad
            "INR" -> R.string.inr
            "CNY" -> R.string.cny
            "GBX" -> R.string.gbx
            else -> null
        }
    }

    fun getFormattedSymbol(symbol: String) = symbol.substringBefore(".")

}