package com.omouravictor.invest_view.data.network.alpha_vantage.model.asset_search

import com.omouravictor.invest_view.ui.wallet.new_asset.model.AssetUiModel

data class AssetMatchesResponse(
    val bestMatches: List<AssetMatchItemResponse>
)

fun AssetMatchesResponse.toAssetUiModelList(): List<AssetUiModel> {
    return bestMatches.map { AssetUiModel(it.symbol, it.name) }
}