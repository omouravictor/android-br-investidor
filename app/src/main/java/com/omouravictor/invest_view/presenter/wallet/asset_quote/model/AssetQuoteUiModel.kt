package com.omouravictor.invest_view.presenter.wallet.asset_quote.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetQuoteUiModel(
    val symbol: String,
    val open: String,
    val high: String,
    val low: String,
    val price: Double,
    val volume: String,
    val latestTradingDay: String,
    val previousClose: String,
    val change: String,
    val changePercent: String
) : Parcelable