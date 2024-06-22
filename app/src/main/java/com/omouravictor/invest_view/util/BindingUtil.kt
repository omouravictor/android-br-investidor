package com.omouravictor.invest_view.util

import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.presenter.wallet.model.getCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedAmount
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedCurrentPosition
import com.omouravictor.invest_view.presenter.wallet.model.getFormattedSymbol

object BindingUtil {

    fun setupVariationViews(
        binding: ItemListAssetBinding,
        currency: String,
        totalInvested: Double,
        totalAssetPrice: Double
    ) {
        val context = binding.root.context
        val variation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val variationFormatted = LocaleUtil.getFormattedCurrencyValue(currency, variation)
        val percentFormatted = LocaleUtil.getFormattedValueForPercent(variation / totalInvested)

        if (variation > 0) {
            val color = ContextCompat.getColor(context, R.color.green)
            binding.tvVariation.setTextColor(color)
            if (totalInvested == 0.0) {
                binding.tvVariation.text = "+$variationFormatted"
                binding.tvVariationPercent.isVisible = false
                binding.ivArrow.isVisible = false
            } else {
                binding.tvVariation.text = "+$variationFormatted ("
                binding.tvVariationPercent.isVisible = true
                binding.tvVariationPercent.setTextColor(color)
                binding.tvVariationPercent.text = "$percentFormatted )"
                binding.ivArrow.isVisible = true
                binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            }
        } else if (variation < 0) {
            val color = ContextCompat.getColor(context, R.color.red)
            binding.tvVariation.setTextColor(color)
            binding.tvVariation.text = "$variationFormatted ("
            binding.tvVariationPercent.isVisible = true
            binding.tvVariationPercent.setTextColor(color)
            binding.tvVariationPercent.text = "$percentFormatted )"
            binding.ivArrow.isVisible = true
            binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
        } else {
            val color = ContextCompat.getColor(context, R.color.gray)
            binding.tvVariation.setTextColor(color)
            binding.tvVariation.text = "$variationFormatted ("
            binding.tvVariationPercent.isVisible = true
            binding.tvVariationPercent.setTextColor(color)
            binding.tvVariationPercent.text = "$percentFormatted)"
            binding.ivArrow.isVisible = false
        }
    }

    fun setupAdapterItemListAssetBinding(
        binding: ItemListAssetBinding,
        assetUiModel: AssetUiModel,
        color: Int
    ) {
        val context = binding.root.context
        binding.color.setBackgroundColor(ContextCompat.getColor(context, color))
        binding.tvSymbol.text = assetUiModel.getFormattedSymbol()
        binding.tvAmount.text = "(${assetUiModel.getFormattedAmount()})"
        binding.tvName.text = assetUiModel.name
        binding.tvTotal.text = assetUiModel.getFormattedCurrentPosition()
        setupVariationViews(
            binding,
            assetUiModel.currency,
            assetUiModel.totalInvested,
            assetUiModel.getCurrentPosition()
        )
    }

}