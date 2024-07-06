package com.omouravictor.invest_view.util

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.LayoutVariationBinding

fun LayoutVariationBinding.calculateAndSetupVariationLayout(
    textSize: Float,
    currency: String,
    reference: Double,
    totalReference: Double
) {
    if (totalReference == 0.0)
        this.setupLayoutVariationVisibility(false)
    else {
        val variation = (reference - totalReference).getRoundedDouble()
        this.setupTextsSize(textSize)
        this.setupLayoutVariationVisibility(true)
        this.setupColorsAndArrow(variation)
        this.tvVariation.text = LocaleUtil.getFormattedCurrencyValue(currency, variation)
        this.tvVariationPercent.text = LocaleUtil.getFormattedPercent(variation / totalReference)
    }
}

fun LayoutVariationBinding.setupTextsSize(textSize: Float) {
    this.tvVariation.textSize = textSize
    this.tvVariationPercent.textSize = textSize
    this.tvBracketStart.textSize = textSize
    this.tvBracketEnd.textSize = textSize
}

fun LayoutVariationBinding.setupLayoutVariationVisibility(isVisible: Boolean, infoMessage: String = "") {
    this.layoutVariation.isVisible = isVisible
    this.tvInfoMessage.isVisible = !isVisible
    this.tvInfoMessage.text = infoMessage
}

fun LayoutVariationBinding.setupTextStyles(typeface: Typeface) {
    this.tvVariation.typeface = typeface
    this.tvVariationPercent.typeface = typeface
    this.tvBracketStart.typeface = typeface
    this.tvBracketEnd.typeface = typeface
}

fun LayoutVariationBinding.setupColorsAndArrow(variation: Double) {
    when {
        variation > 0 -> {
            val color = ContextCompat.getColor(this.root.context, R.color.green)
            this.tvVariation.setTextColor(color)
            this.tvVariationPercent.setTextColor(color)
            this.tvBracketStart.setTextColor(color)
            this.tvBracketEnd.setTextColor(color)
            this.ivArrow.isVisible = true
            this.ivArrow.setImageResource(R.drawable.ic_arrow_up)
        }

        variation < 0 -> {
            val color = ContextCompat.getColor(this.root.context, R.color.red)
            this.tvVariation.setTextColor(color)
            this.tvVariationPercent.setTextColor(color)
            this.tvBracketStart.setTextColor(color)
            this.tvBracketEnd.setTextColor(color)
            this.ivArrow.isVisible = true
            this.ivArrow.setImageResource(R.drawable.ic_arrow_down)
        }

        else -> {
            val color = ContextCompat.getColor(this.root.context, R.color.gray)
            this.tvVariation.setTextColor(color)
            this.tvVariationPercent.setTextColor(color)
            this.tvBracketStart.setTextColor(color)
            this.tvBracketEnd.setTextColor(color)
            this.ivArrow.isVisible = false
        }
    }
}