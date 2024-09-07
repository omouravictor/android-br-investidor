package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.AssetsBySearchResponse

interface AssetsApiRepository {
    suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse>
    suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse>
}