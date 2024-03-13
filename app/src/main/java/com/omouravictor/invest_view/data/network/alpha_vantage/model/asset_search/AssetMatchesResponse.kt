package com.omouravictor.invest_view.data.network.alpha_vantage.model.asset_search

import com.omouravictor.invest_view.ui.wallet.assets.asset_search.model.AssetMatchUi

data class AssetMatchesResponse(
    val bestMatches: List<AssetMatchResponse>
)

fun AssetMatchesResponse.toAssetMatchesUi(): List<AssetMatchUi> {
    return bestMatches.map {
        AssetMatchUi(
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