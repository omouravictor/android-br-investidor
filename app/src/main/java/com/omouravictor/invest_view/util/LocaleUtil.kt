package com.omouravictor.invest_view.util

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Currency
import java.util.Locale
import java.util.TimeZone

object LocaleUtil {

    val appLocale = Locale("pt", "BR")
    private val appTimeZone = TimeZone.getTimeZone("America/Sao_Paulo")
    private val appDateTimeFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", appLocale)
    private val isoDateTimeFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", appLocale)

    fun getFormattedCurrencyValue(currency: String, value: Number): String {
        val currencyFormat = NumberFormat.getCurrencyInstance(appLocale)
        currencyFormat.currency = Currency.getInstance(currency)
        return currencyFormat.format(value)
    }

    fun getFormattedLong(value: Long): String {
        val intNumberFormat = NumberFormat.getIntegerInstance(appLocale)
        return intNumberFormat.format(value)
    }

    fun getFormattedPercent(value: Double): String {
        val percentFormat = NumberFormat.getPercentInstance(appLocale)
        percentFormat.maximumFractionDigits = 2
        return percentFormat.format(value)
    }

    fun getFormattedDateTime(dateTimeString: String?): String {
        if (dateTimeString.isNullOrEmpty()) return ""

        val date = isoDateTimeFormat.parse(dateTimeString) ?: return ""
        date.time += appTimeZone.rawOffset

        return appDateTimeFormat.format(date)
    }

}