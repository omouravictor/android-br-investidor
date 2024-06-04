package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetQuoteUiModel(
    val symbol: String = "",
    val open: Double = 0.0,
    val high: Double = 0.0,
    val low: Double = 0.0,
    val price: Double = 0.0,
    val volume: Int,
    val latestTradingDay: String = "",
    val previousClose: Double = 0.0,
    val change: Double = 0.0,
    val changePercent: String = ""
) : Parcelable