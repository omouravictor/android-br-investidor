package com.omouravictor.invest_view.data.network.remote.model.assets_by_search

import com.omouravictor.invest_view.presenter.wallet.model.AssetBySearchUiModel

data class AssetsBySearchResponse(
    val bestMatches: List<AssetBySearchItemResponse>
)

fun AssetsBySearchResponse.toAssetsBySearchUiModel(): List<AssetBySearchUiModel> {
    return bestMatches.map {
        AssetBySearchUiModel(
            symbol = it.symbol,
            name = it.name,
            type = it.type,
            region = it.region,
            currency = it.currency
        )
    }
}