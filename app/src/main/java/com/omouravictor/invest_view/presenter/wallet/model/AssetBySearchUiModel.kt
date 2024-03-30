package com.omouravictor.invest_view.presenter.wallet.model

import android.os.Parcelable
import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import kotlinx.parcelize.Parcelize

@Parcelize
data class AssetBySearchUiModel(
    val symbol: String,
    val name: String,
    val type: String,
    val region: String,
    val currency: String,
    var price: Double = 0.0,
) : Parcelable

fun AssetBySearchUiModel.getDisplaySymbol() = symbol.substringBeforeLast(".")

fun AssetBySearchUiModel.getAssetType(): AssetTypes {
    val assetType = type.replace(" ", "_").uppercase()
    return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
}