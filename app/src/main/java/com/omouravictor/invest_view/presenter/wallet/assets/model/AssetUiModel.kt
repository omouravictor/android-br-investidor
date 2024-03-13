package com.omouravictor.invest_view.presenter.wallet.assets.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetUiModel(
    val symbol: String,
    val name: String
) : Parcelable
