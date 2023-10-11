package com.omouravictor.invest_view.ui.wallet.new_asset

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetTypeUiModel(
    val code: Int,
    val name: String,
    val color: Int
) : Parcelable
