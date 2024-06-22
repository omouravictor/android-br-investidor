package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.asset_types.AssetTypes
import com.omouravictor.invest_view.util.AssetUtil
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedCurrencyValue
import com.omouravictor.invest_view.util.LocaleUtil.getFormattedValueForLongNumber
import com.omouravictor.invest_view.util.NumberUtil.getRoundedDouble
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetUiModel(
    val symbol: String = "",
    val name: String = "",
    val assetType: AssetTypes = AssetTypes.OTHER,
    val region: String = "",
    val currency: String = "",
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
) : Parcelable

fun AssetUiModel.getFormattedSymbol() = AssetUtil.getFormattedSymbol(symbol)

fun AssetUiModel.getCurrentPosition() = price * amount

fun AssetUiModel.getFormattedAmount() = getFormattedValueForLongNumber(amount)

fun AssetUiModel.getFormattedAssetPrice() = getFormattedCurrencyValue(currency, price)

fun AssetUiModel.getFormattedCurrentPosition() = getFormattedCurrencyValue(currency, getCurrentPosition())

fun AssetUiModel.getFormattedTotalInvested() = getFormattedCurrencyValue(currency, totalInvested)

fun AssetUiModel.getFormattedYield(): String {
    val variation = getRoundedDouble(getCurrentPosition() - totalInvested)
    val variationFormatted = getFormattedCurrencyValue(currency, variation)
    return if (variation > 0) "+$variationFormatted" else variationFormatted
}