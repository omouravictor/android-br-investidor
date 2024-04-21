package com.omouravictor.invest_view.util

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes

object AssetUtil {

    fun getAssetType(type: String): AssetTypes {
        val assetType = type.replace(" ", "_").uppercase()
        return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
    }

    fun getFormattedSymbol(symbol: String): String {
        return symbol.substringBeforeLast(".")
    }

    @SuppressLint("SetTextI18n")
    fun setupVariationViews(
        binding: ItemListAssetBinding,
        currency: String,
        totalInvested: Double,
        totalAssetPrice: Double
    ) {
        val context = binding.root.context
        val variation = NumberUtil.getRoundedDouble(totalAssetPrice - totalInvested)
        val variationFormatted = LocaleUtil.getFormattedValueForCurrency(currency, variation)
        val percentFormatted = LocaleUtil.getFormattedValueForPercent(variation / totalInvested)

        if (variation > 0) {
            val color = ContextCompat.getColor(context, R.color.green)
            binding.ivArrow.isVisible = true
            binding.ivArrow.setImageResource(R.drawable.ic_arrow_up)
            binding.tvVariation.setTextColor(color)
            binding.tvVariationPercent.setTextColor(color)
        } else if (variation < 0) {
            val color = ContextCompat.getColor(context, R.color.red)
            binding.ivArrow.isVisible = true
            binding.ivArrow.setImageResource(R.drawable.ic_arrow_down)
            binding.tvVariation.setTextColor(color)
            binding.tvVariationPercent.setTextColor(color)
        } else {
            val color = ContextCompat.getColor(context, R.color.gray)
            binding.ivArrow.isVisible = false
            binding.tvVariation.setTextColor(color)
            binding.tvVariationPercent.setTextColor(color)
        }

        binding.tvVariation.text = if (variation > 0) "+$variationFormatted (" else "$variationFormatted ("
        binding.tvVariationPercent.text = if (variation != 0.0) "$percentFormatted )" else "$percentFormatted)"
    }

}