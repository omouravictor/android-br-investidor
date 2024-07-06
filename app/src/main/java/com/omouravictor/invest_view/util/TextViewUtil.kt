package com.omouravictor.invest_view.util

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R

@SuppressLint("SetTextI18n")
fun TextView.setupVariationTextView(currency: String, variation: Double, variationPercent: Double?) {
    val formattedValue = LocaleUtil.getFormattedCurrencyValue(currency, variation)
        .let { if (variation > 0) "+$it" else it }

    val formattedPercent = variationPercent?.let {
        if (variation > 0) "+${LocaleUtil.getFormattedPercent(it)}"
        else LocaleUtil.getFormattedPercent(it)
    }

    text = if (formattedPercent != null) "$formattedValue ($formattedPercent)" else formattedValue

    setTextColor(
        ContextCompat.getColor(
            context, when {
                variation > 0 -> R.color.green
                variation < 0 -> R.color.red
                else -> R.color.gray
            }
        )
    )
}