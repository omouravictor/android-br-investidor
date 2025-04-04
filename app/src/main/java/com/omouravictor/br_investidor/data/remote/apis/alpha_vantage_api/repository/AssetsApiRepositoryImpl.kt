package com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.repository

import android.util.Log
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.AlphaVantageApi
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetGlobalQuoteResponse
import com.omouravictor.br_investidor.data.remote.apis.alpha_vantage_api.model.AssetsBySearchResponse
import com.omouravictor.br_investidor.di.model.DispatcherProvider
import kotlinx.coroutines.withContext

class AssetsApiRepositoryImpl(
    private val dispatchers: DispatcherProvider,
    private val alphaVantageApi: AlphaVantageApi
) : AssetsApiRepository {

    override suspend fun getAssetsBySearch(keywords: String): Result<AssetsBySearchResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageApi.getAssetsBySearch(keywords)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetsBySearch", "Keywords: $keywords", e)
                Result.failure(e)
            }
        }
    }

    override suspend fun getAssetGlobalQuote(symbol: String): Result<AssetGlobalQuoteResponse> {
        return withContext(dispatchers.io) {
            try {
                val response = alphaVantageApi.getAssetGlobalQuote(symbol)
                Result.success(response)
            } catch (e: Exception) {
                Log.e("GetAssetGlobalQuote", "Symbol: $symbol", e)
                Result.failure(e)
            }
        }
    }

}