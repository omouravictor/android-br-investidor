package com.omouravictor.invest_view.data.network.alpha_vantage.asset_search

import com.omouravictor.invest_view.presenter.wallet.assets.model.AssetUiModel

data class AssetMatchesResponse(
    val bestMatches: List<AssetMatchItemResponse>
)

fun AssetMatchesResponse.toAssetUiModelList(): List<AssetUiModel> {
    return bestMatches.map { AssetUiModel(it.symbol, it.name) }
}