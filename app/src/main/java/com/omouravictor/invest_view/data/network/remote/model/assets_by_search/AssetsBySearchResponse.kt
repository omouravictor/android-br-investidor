package com.omouravictor.invest_view.data.network.remote.model.assets_by_search

import com.omouravictor.invest_view.presenter.wallet.base.AssetTypes
import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel

data class AssetsBySearchResponse(
    val bestMatches: List<AssetBySearchItemResponse>
)

fun AssetsBySearchResponse.toAssetsBySearchUiModel(): List<AssetBySearchUiModel> {
    return bestMatches.map {
        AssetBySearchUiModel(
            symbol = it.symbol,
            name = it.name,
            assetType = getAssetType(it.type),
            currency = it.currency
        )
    }
}

private fun getAssetType(type: String): AssetTypes {
    val assetType = type.replace(" ", "_").uppercase()
    return AssetTypes.values().firstOrNull { it.name == assetType } ?: AssetTypes.OTHER
}