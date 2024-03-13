package com.omouravictor.invest_view.ui.wallet.assets.asset_types.model

import android.content.res.ColorStateList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUi(
    val assetTypes: AssetTypes,
    val description: String,
    val color: ColorStateList
) : Parcelable
