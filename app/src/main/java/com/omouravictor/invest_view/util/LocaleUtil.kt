package com.omouravictor.invest_view.util

import java.text.NumberFormat
import java.util.Locale

object LocaleUtil {

    fun getFormattedValueForCurrencyLocale(locale: Locale, value: Double): String =
        NumberFormat.getCurrencyInstance(locale).format(value)

}