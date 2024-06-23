package com.omouravictor.invest_view.util

import android.view.View
import androidx.core.content.ContextCompat
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.LayoutVariationBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getCurrentPosition
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForPercent

object BindingUtil {

    fun setupVariationLayout(
        binding: LayoutVariationBinding,
        currency: String,
        totalInvested: Double,
        totalAssetPrice: Double
    ) {
        val variation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val formattedVariation = getFormattedCurrencyValue(currency, variation)
        val formattedVariationPercent = getFormattedValueForPercent(variation / totalInvested)

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

        binding.tvVariation.text = formattedVariation
        binding.tvVariationPercent.text = formattedVariationPercent
    }

    fun setupVariationLayout(binding: LayoutVariationBinding, assetUiModel: AssetUiModel) {
        setupVariationLayout(
            binding,
            assetUiModel.currency,
            assetUiModel.totalInvested,
            assetUiModel.getCurrentPosition()
        )
    }

}