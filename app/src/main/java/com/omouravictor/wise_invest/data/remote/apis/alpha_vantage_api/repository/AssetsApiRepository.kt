package com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.repository

import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.AssetGlobalQuoteResponse
import com.omouravictor.wise_invest.data.remote.apis.alpha_vantage_api.model.AssetsBySearchResponse

interface AssetsApiRepository {
    suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse>
    suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse>
}