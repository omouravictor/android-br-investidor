package com.omouravictor.invest_view.ui.wallet.assets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class AssetUiModel(
    val currencyName: String,
    val currencyTerm: String,
    val unitaryRate: Double,
    val variation: Double,
    val rateDate: Date
) : Parcelable
