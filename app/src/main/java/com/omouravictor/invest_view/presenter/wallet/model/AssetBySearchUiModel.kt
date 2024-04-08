package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetBySearchUiModel(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val currency: String,
    var price: Double = 0.0,
) : Parcelable