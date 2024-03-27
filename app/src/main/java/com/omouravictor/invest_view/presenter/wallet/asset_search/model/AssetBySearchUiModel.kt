package com.omouravictor.invest_view.presenter.wallet.asset_search.model

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.model.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetBySearchUiModel(
    val symbol: String,
    val name: String,
    val assetType: AssetTypes,
    var price: Double = 0.0,
) : Parcelable