package com.omouravictor.invest_view.ui.wallet.assets.asset_search.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetMatchUi(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val marketOpen: String,
    val marketClose: String,
    val timezone: String,
    val currency: String,
    val matchScore: String
) : Parcelable
