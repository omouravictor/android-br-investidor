package com.omouravictor.invest_view.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object LocaleUtil {

    private val globalLocale = Locale("pt", "BR")

    fun getFormattedValueForCurrencyLocale(currency: String, value: Number): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(globalLocale)
        currencyFormat.currency = Currency.getInstance(currency)
        return currencyFormat.format(value)
    }

}