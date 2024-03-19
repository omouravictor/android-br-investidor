package com.omouravictor.invest_view.data.network.remote.model.assetsbysearch

import com.omouravictor.invest_view.presenter.wallet.asset_search.model.AssetBySearchUiModel

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
            marketOpen = it.marketOpen,
            marketClose = it.marketClose,
            timezone = it.timezone,
            currency = it.currency,
            matchScore = it.matchScore
        )
    }
}