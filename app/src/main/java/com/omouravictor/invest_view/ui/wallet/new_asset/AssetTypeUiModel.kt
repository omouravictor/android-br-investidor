package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Parcelable
import com.omouravictor.invest_view.ui.wallet.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUiModel(
    val assetType: AssetTypes,
    val description: String,
    val color: Int
) : Parcelable
