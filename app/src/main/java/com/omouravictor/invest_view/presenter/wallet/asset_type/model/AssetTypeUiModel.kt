package com.omouravictor.invest_view.presenter.wallet.asset_type.model

import android.content.res.ColorStateList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUiModel(
    val assetType: AssetType,
    val description: String,
    val color: ColorStateList
) : Parcelable
