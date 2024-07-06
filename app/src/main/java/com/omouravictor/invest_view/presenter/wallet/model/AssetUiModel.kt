package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.LocaleUtil
import com.omouravictor.invest_view.util.getRoundedDouble
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetUiModel(
    val symbol: String = "",
    val name: String = "",
    val originalType: String = "",
    val assetType: AssetTypes = AssetTypes.OTHER,
    val region: String = "",
    val currency: String = "",
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
) : Parcelable

fun AssetUiModel.getTotalPrice() = price * amount

fun AssetUiModel.getYield() = (getTotalPrice() - totalInvested).getRoundedDouble()

fun AssetUiModel.getYieldPercent() = getYield() / totalInvested

fun AssetUiModel.getFormattedSymbol() = AssetUtil.getFormattedSymbol(symbol)

fun AssetUiModel.getFormattedAmount() = LocaleUtil.getFormattedLong(amount)

fun AssetUiModel.getFormattedSymbolAndAmount() = "${getFormattedSymbol()} (${getFormattedAmount()})"

fun AssetUiModel.getFormattedAssetPrice() = LocaleUtil.getFormattedCurrencyValue(currency, price)

fun AssetUiModel.getFormattedTotalPrice() = LocaleUtil.getFormattedCurrencyValue(currency, getTotalPrice())

fun AssetUiModel.getFormattedTotalInvested() = LocaleUtil.getFormattedCurrencyValue(currency, totalInvested)

fun AssetUiModel.getFormattedYield(yield: Double): String {
    if (totalInvested == 0.0) return ""
    val formattedYield = LocaleUtil.getFormattedCurrencyValue(currency, yield)
    val formattedPercent = LocaleUtil.getFormattedPercent(getYieldPercent())

    return if (yield > 0)
        "+$formattedYield (+$formattedPercent)"
    else
        "$formattedYield ($formattedPercent)"
}
