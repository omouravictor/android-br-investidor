package com.omouravictor.invest_view.data.remote.model.assets_by_search

import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel
import com.omouravictor.invest_view.util.AssetUtil

data class AssetsBySearchResponse(
    val bestMatches: List<BestMatchResponse>
)

fun AssetsBySearchResponse.toAssetsUiModel(): List<AssetUiModel> {
    return bestMatches.map {
        AssetUiModel(
            symbol = it.symbol,
            name = it.name,
            type = AssetUtil.getAssetType(it.symbol, it.type),
            region = it.region,
            currency = it.currency
        )
    }
}