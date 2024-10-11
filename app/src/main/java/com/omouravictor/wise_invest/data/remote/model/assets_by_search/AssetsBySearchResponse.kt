package com.omouravictor.wise_invest.data.remote.model.assets_by_search

import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel
import com.omouravictor.wise_invest.util.AssetUtil

data class AssetsBySearchResponse(
    val bestMatches: List<BestMatchResponse>
)

fun AssetsBySearchResponse.toAssetsUiModel(): List<AssetUiModel> {
    return bestMatches.map {
        AssetUiModel(
            symbol = it.symbol ?: "",
            name = it.name ?: "",
            type = AssetUtil.getAssetType(it.symbol ?: "", it.type ?: ""),
            region = it.region ?: "",
            currency = it.currency ?: ""
        )
    }
}