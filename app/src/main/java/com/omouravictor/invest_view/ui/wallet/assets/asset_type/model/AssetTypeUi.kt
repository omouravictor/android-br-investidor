package com.omouravictor.invest_view.ui.wallet.assets.asset_type.model

import android.content.res.ColorStateList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUi(
    val assetType: AssetType,
    val description: String,
    val color: ColorStateList
) : Parcelable
