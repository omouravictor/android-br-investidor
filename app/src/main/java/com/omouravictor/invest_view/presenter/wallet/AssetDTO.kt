package com.omouravictor.invest_view.presenter.wallet

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.model.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetDTO(
    var symbol: String = "",
    var name: String = "",
    var price: Double = 0.0,
    var assetTypes: AssetTypes = AssetTypes.OTHERS,
) : Parcelable