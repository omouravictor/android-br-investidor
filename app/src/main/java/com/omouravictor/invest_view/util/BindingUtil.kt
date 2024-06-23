package com.omouravictor.invest_view.util

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.LayoutVariationBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getPriceCurrentPosition
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent

object BindingUtil {

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

    fun calculateAndSetupVariationLayout(
        binding: LayoutVariationBinding,
        currency: String,
        reference: Double,
        totalReference: Double
    ) {
        val variation = NumberUtil.getRoundedDouble(reference - totalReference)
        setupVariationTexts(binding, currency, variation, totalReference)
        setupVisibilityOfViews(binding, true)
        setupColorsAndArrow(binding, variation)
    }

    fun calculateAndSetupVariationLayout(binding: LayoutVariationBinding, assetUiModel: AssetUiModel) {
        if (assetUiModel.totalInvested == 0.0)
            setupVisibilityOfViews(binding, false)
        else {
            calculateAndSetupVariationLayout(
                binding = binding,
                currency = assetUiModel.currency,
                reference = assetUiModel.getPriceCurrentPosition(),
                totalReference = assetUiModel.totalInvested
            )
        }
    }

    private fun setupVisibilityOfViews(binding: LayoutVariationBinding, isVisible: Boolean) {
        binding.tvVariation.isVisible = isVisible
        binding.tvVariationPercent.isVisible = isVisible
        binding.tvBracketStart.isVisible = isVisible
        binding.tvBracketEnd.isVisible = isVisible
        binding.ivArrow.isVisible = isVisible
    }

    private fun setupVariationTexts(
        binding: LayoutVariationBinding,
        currency: String,
        variation: Double,
        totalReference: Double
    ) {
        binding.tvVariation.text = getFormattedCurrencyValue(currency, variation)
        binding.tvVariationPercent.text = getFormattedValueForPercent(variation / totalReference)
    }

}