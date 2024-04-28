package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetQuoteUiModel(
    val symbol: String,
    val open: Float,
    val high: Float,
    val low: Float,
    val price: Float,
    val volume: Int,
    val latestTradingDay: String,
    val previousClose: Float,
    val change: Float,
    val changePercent: String
) : Parcelable