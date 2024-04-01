package com.omouravictor.invest_view.util

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

object LocaleUtil {

    private val appLocale = Locale("pt", "BR")

    fun getFormattedValueForCurrency(currency: String, value: Number): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(appLocale)
        currencyFormat.currency = Currency.getInstance(currency)
        return currencyFormat.format(value)
    }

    fun getFormattedValueForIntNumber(value: Int): String {
        val intNumberFormat = NumberFormat.getIntegerInstance(appLocale)
        return intNumberFormat.format(value)
    }

    fun getFormattedValueForPercent(value: Double): String {
        val percentFormat = NumberFormat.getPercentInstance(appLocale)
        percentFormat.minimumFractionDigits = 1
        percentFormat.maximumFractionDigits = 2
        return percentFormat.format(value)
    }

}