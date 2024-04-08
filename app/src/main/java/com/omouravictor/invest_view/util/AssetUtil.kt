package com.omouravictor.invest_view.util

object AssetUtil {

    fun getDisplaySymbol(symbol: String) = symbol.substringBeforeLast(".")

}