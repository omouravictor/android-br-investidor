package com.omouravictor.invest_view.ui.wallet.asset_type

import android.content.res.ColorStateList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUiModel(
    val assetType: AssetTypes,
    val description: String,
    val color: ColorStateList?
) : Parcelable
