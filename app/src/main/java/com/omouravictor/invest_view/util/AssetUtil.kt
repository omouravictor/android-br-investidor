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
        val (colorRes, arrowRes) = when {
            variation > 0 -> Pair(R.color.green, R.drawable.ic_arrow_up)
            variation < 0 -> Pair(R.color.red, R.drawable.ic_arrow_down)
            else -> Pair(R.color.gray, null)
        }
        val color = ContextCompat.getColor(context, colorRes)
        val variationFormatted = LocaleUtil.getFormattedValueForCurrency(currency, variation)
        val percentFormatted = LocaleUtil.getFormattedValueForPercent(variation / totalInvested)

        binding.ivArrow.isVisible = arrowRes != null
        arrowRes?.let { binding.ivArrow.setImageResource(it) }
        binding.tvVariation.setTextColor(color)
        binding.tvVariationPercent.setTextColor(color)
        binding.tvVariation.text = if (variation > 0) "+$variationFormatted (" else "$variationFormatted ("
        binding.tvVariationPercent.text = if (variation != 0.0) "$percentFormatted )" else "$percentFormatted)"
    }

}