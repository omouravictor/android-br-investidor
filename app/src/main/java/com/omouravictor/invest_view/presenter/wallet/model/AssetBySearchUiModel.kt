package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetBySearchUiModel(
    val symbol: String,
    val companyName: String,
    val assetType: AssetTypes,
    var price: Double = 0.0,
) : Parcelable