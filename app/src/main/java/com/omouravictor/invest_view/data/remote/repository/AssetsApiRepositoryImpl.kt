package com.omouravictor.invest_view.data.remote.repository

import android.util.Log
import com.omouravictor.invest_view.data.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.remote.model.assets_by_search.AssetsBySearchResponse
import com.omouravictor.invest_view.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class AssetsApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val alphaVantageService: AlphaVantageService
) : AssetsApiRepository {

    override suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageService.getAssetsBySearch(keywords)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetsBySearch", "Keywords $keywords", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageService.getAssetGlobalQuote(symbol)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetGlobalQuote", "Symbol $symbol", e)
                Result.failure(e)
            }
        }
    }

}