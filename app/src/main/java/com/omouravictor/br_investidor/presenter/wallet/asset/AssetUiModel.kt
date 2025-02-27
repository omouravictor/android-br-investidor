package com.omouravictor.br_investidor.presenter.wallet.asset

import android.os.Parcelable
import com.omouravictor.br_investidor.presenter.wallet.asset_types.AssetType
import com.omouravictor.br_investidor.util.AssetUtil
import com.omouravictor.br_investidor.util.LocaleUtil
import com.omouravictor.br_investidor.util.getRoundedDouble
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetUiModel(
    val symbol: String = "",
    val name: String = "",
    val type: AssetType = AssetType.DEFAULT,
    val region: String = "",
    val currency: String = "",
    var price: Double = 0.0,
    var amount: Long = 0,
    var totalInvested: Double = 0.0
) : Parcelable

fun AssetUiModel.getTotalPrice() = price * amount

fun AssetUiModel.getYield() = if (totalInvested == 0.0) null else (getTotalPrice() - totalInvested).getRoundedDouble()

fun AssetUiModel.getFormattedSymbol() = AssetUtil.getFormattedSymbol(symbol)

fun AssetUiModel.getFormattedAmount() = LocaleUtil.getFormattedLong(amount)

fun AssetUiModel.getFormattedSymbolAndAmount() = "${getFormattedSymbol()} (${getFormattedAmount()})"

fun AssetUiModel.getFormattedAssetPrice() = LocaleUtil.getFormattedCurrencyValue(currency, price)

fun AssetUiModel.getFormattedTotalPrice() = LocaleUtil.getFormattedCurrencyValue(currency, getTotalPrice())

fun AssetUiModel.getFormattedTotalInvested() = LocaleUtil.getFormattedCurrencyValue(currency, totalInvested)