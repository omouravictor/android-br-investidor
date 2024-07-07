package com.omouravictor.invest_view.util

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getYield
import com.omouravictor.invest_view.presenter.wallet.model.getYieldPercent
import java.lang.Double.NEGATIVE_INFINITY
import java.lang.Double.POSITIVE_INFINITY

@SuppressLint("SetTextI18n")
fun TextView.setupVariation(currency: String, variation: Double, variationPercent: Double?) {
    if (variationPercent == POSITIVE_INFINITY || variationPercent == NEGATIVE_INFINITY) {
        text = ""
        return
    }

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

@SuppressLint("SetTextI18n")
fun TextView.setupYieldForAsset(assetUiModel: AssetUiModel) {
    if (assetUiModel.totalInvested == 0.0) {
        text = ""
        return
    }

    val yield = assetUiModel.getYield()
    val formattedValue = LocaleUtil.getFormattedCurrencyValue(assetUiModel.currency, yield)
    val formattedPercent = LocaleUtil.getFormattedPercent(assetUiModel.getYieldPercent())

    text = if (yield > 0) {
        "+$formattedValue (+$formattedPercent)"
    } else {
        "$formattedValue ($formattedPercent)"
    }

    setTextColor(
        ContextCompat.getColor(
            context, when {
                yield > 0 -> R.color.green
                yield < 0 -> R.color.red
                else -> R.color.gray
            }
        )
    )
}