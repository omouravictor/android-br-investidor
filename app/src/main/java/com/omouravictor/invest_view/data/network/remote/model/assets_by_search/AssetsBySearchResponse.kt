package com.omouravictor.invest_view.data.network.remote.model.assets_by_search

import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import com.omouravictor.invest_view.util.AssetUtil

data class AssetsBySearchResponse(
    val bestMatches: List<AssetBySearchItemResponse>
)

fun AssetsBySearchResponse.toAssetsUiModel(): List<AssetUiModel> {
    return bestMatches.map {
        AssetUiModel(
            symbol = it.symbol,
            name = it.name,
            originalType = it.type,
            assetType = AssetUtil.getAssetType(it.symbol, it.type),
            region = it.region,
            currency = it.currency
        )
    }
}