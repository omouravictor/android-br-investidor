package com.omouravictor.invest_view.presenter.wallet.model

import android.content.res.ColorStateList
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUiModel(
    val assetTypes: AssetTypes,
    val description: String,
    val color: ColorStateList
) : Parcelable
