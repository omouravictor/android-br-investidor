package com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.repository

import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetGlobalQuoteResponse
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetsBySearchResponse

interface AssetsApiRepository {
    suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse>
    suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse>
}