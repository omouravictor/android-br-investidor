package com.omouravictor.invest_view.ui.wallet.new_asset.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetUiModel(
    val symbol: String,
    val name: String
) : Parcelable
