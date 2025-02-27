package com.omouravictor.br_investidor.util

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.omouravictor.br_investidor.R
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel
import com.omouravictor.br_investidor.presenter.wallet.asset.getYield
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

    text = if (formattedPercent != null) {
        "$formattedValue ($formattedPercent)"
    } else {
        formattedValue
    }

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
    val yield = assetUiModel.getYield()

    if (yield == null) {
        text = ""
        return
    }

    val formattedValue = LocaleUtil.getFormattedCurrencyValue(assetUiModel.currency, yield)
    val formattedPercent = LocaleUtil.getFormattedPercent(yield / assetUiModel.totalInvested)

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