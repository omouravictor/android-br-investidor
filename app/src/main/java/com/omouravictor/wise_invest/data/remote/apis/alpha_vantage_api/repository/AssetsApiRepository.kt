package com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.repository

import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.assets_by_search.AssetsBySearchResponse

interface AssetsApiRepository {
    suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse>
    suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse>
}