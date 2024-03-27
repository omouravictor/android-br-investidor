package com.omouravictor.invest_view.presenter.wallet.asset_quote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetQuoteUiModel(
    val symbol: String,
    val open: Double,
    val high: Double,
    val low: Double,
    val price: Double,
    val volume: Int,
    val latestTradingDay: String,
    val previousClose: Double,
    val change: Double,
    val changePercent: String
) : Parcelable