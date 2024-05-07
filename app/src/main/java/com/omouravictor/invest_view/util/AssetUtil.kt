package com.omouravictor.invest_view.util

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.omouravictor.invest_view.R
import com.omouravictor.invest_view.databinding.ItemListAssetBinding
import com.omouravictor.invest_view.presenter.base.AssetTypes

object AssetUtil {

    fun getAssetType(symbol: String, type: String): AssetTypes {
        return when (type) {
            "Equity" -> {
                if (symbol.substringAfter(".") == "SAO") {
                    val formattedSymbol = getFormattedSymbol(symbol)
                    val lastTwoDigits = formattedSymbol.takeLast(2)
                    if (lastTwoDigits == "34" || lastTwoDigits == "35" || lastTwoDigits == "32" || lastTwoDigits == "33")
                        AssetTypes.BDR
                    else
                        AssetTypes.BRAZILIAN_STOCK
                } else {
                    AssetTypes.STOCK
                }
            }

            "Mutual Fund" -> AssetTypes.FI
            "ETF" -> AssetTypes.ETF
            else -> AssetTypes.OTHER
        }
    }

    fun getFormattedSymbol(symbol: String): String {
        return symbol.substringBefore(".")
    }

    @SuppressLint("SetTextI18n")
    fun setupVariationViews(
        binding: ItemListAssetBinding,
        currency: String,
        totalInvested: Double,
        totalAssetPrice: Double
    ) {
        if (totalInvested > 0) {
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

        } else {
            binding.tvVariation.text = ""
            binding.tvVariationPercent.text = ""
            binding.ivArrow.isVisible = false
        }
    }

}