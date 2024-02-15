package com.omouravictor.invest_view.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

object FormatUtils {

    object BrazilianFormats {
        private val brLocale = Locale("pt", "BR")
        private val brDecimalFormatSymbols: DecimalFormatSymbols = DecimalFormatSymbols(brLocale)
        val brTimeFormat = SimpleDateFormat("HH:mm", brLocale)
        val brDateFormat = SimpleDateFormat("dd/MM/yy", brLocale)
        val brCurrencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(brLocale)
        val brDecimalFormat: DecimalFormat = DecimalFormat("#,##0.00", brDecimalFormatSymbols)
    }

    fun getFormattedValueForCurrencyLocale(value: Double, locale: Locale): String =
        NumberFormat.getCurrencyInstance(locale).format(value)

}