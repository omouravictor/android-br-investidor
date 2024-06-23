package com.omouravictor.invest_view.util

import android.view.View
import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.LayoutVariationBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getPriceCurrentPosition
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent

object BindingUtil {

    fun setupColorsAndVisibilitiesOnVariationLayout(binding: LayoutVariationBinding, variation: Double) {
        when {
            variation > 0 -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.green)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.tvBracketStart.visibility = View.VISIBLE
                binding.tvBracketEnd.visibility = View.VISIBLE
                binding.ivArrow.visibility = View.VISIBLE
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            }

            variation < 0 -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.red)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.tvBracketStart.visibility = View.VISIBLE
                binding.tvBracketEnd.visibility = View.VISIBLE
                binding.ivArrow.visibility = View.VISIBLE
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
            }

            else -> {
                val color = ContextCompat.getColor(binding.root.context, R.color.gray)
                binding.tvVariation.setTextColor(color)
                binding.tvVariationPercent.setTextColor(color)
                binding.tvBracketStart.setTextColor(color)
                binding.tvBracketEnd.setTextColor(color)
                binding.ivArrow.visibility = View.GONE
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
        binding.tvVariation.text = getFormattedCurrencyValue(currency, variation)
        binding.tvVariationPercent.text = getFormattedValueForPercent(variation / totalReference)
        setupColorsAndVisibilitiesOnVariationLayout(binding, variation)
    }

    fun calculateAndSetupVariationLayout(binding: LayoutVariationBinding, assetUiModel: AssetUiModel) {
        calculateAndSetupVariationLayout(
            binding = binding,
            currency = assetUiModel.currency,
            reference = assetUiModel.getPriceCurrentPosition(),
            totalReference = assetUiModel.totalInvested
        )
    }

}