package com.omouravictor.invest_view.util

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.LayoutVariationBinding
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent

object BindingUtil {

    fun calculateAndSetupVariationLayout(
        binding: LayoutVariationBinding,
        textSize: Float,
        currency: String,
        reference: Double,
        totalReference: Double
    ) {
        if (totalReference == 0.0)
            setupVisibilities(binding, false)
        else {
            val variation = NumberUtil.getRoundedDouble(reference - totalReference)
            setupTexts(binding, textSize, currency, variation, totalReference)
            setupVisibilities(binding, true)
            setupColorsAndArrow(binding, variation)
        }
    }

    fun setupColorsAndArrow(binding: LayoutVariationBinding, variation: Double) {
        when {
            variation > 0 -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.green)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.ivArrow.isVisible = true
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            }

            variation < 0 -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.red)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.ivArrow.isVisible = true
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
            }

            else -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.gray)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.ivArrow.isVisible = false
            }
        }
    }

    private fun setupVisibilities(binding: LayoutVariationBinding, isVisible: Boolean) {
        binding.tvVariation.isVisible = isVisible
        binding.tvVariationPercent.isVisible = isVisible
        binding.tvBracketStart.isVisible = isVisible
        binding.tvBracketEnd.isVisible = isVisible
        binding.ivArrow.isVisible = isVisible
    }

    private fun setupTexts(
        binding: LayoutVariationBinding,
        textSize: Float,
        currency: String,
        variation: Double,
        totalReference: Double
    ) {
        binding.tvVariation.text = getFormattedCurrencyValue(currency, variation)
        binding.tvVariationPercent.text = getFormattedValueForPercent(variation / totalReference)

        binding.tvVariation.textSize = textSize
        binding.tvVariationPercent.textSize = textSize
        binding.tvBracketStart.textSize = textSize
        binding.tvBracketEnd.textSize = textSize
    }

}