package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse

class AssetsApiRepositoryImpl(
    private val alphaVantageService: AlphaVantageService
) : AssetsApiRepository {

    override suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse> {
        return try {
            val response = alphaVantageService.getAssetsBySearch(keywords)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse> {
        return try {
            val response = alphaVantageService.getAssetGlobalQuote(symbol)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}