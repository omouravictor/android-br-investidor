package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteAssetsRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    RemoteAssetsRepository {

    override suspend fun getAssetsBySearch(keywords: String): Flow<NetworkState<AssetsBySearchResponse>> {
        return flow {
            emit(NetworkState.Loading)
            try {
                val response = alphaVantageService.getAssetsBySearch(keywords)
                emit(NetworkState.Success(response))
            } catch (e: Exception) {
                emit(NetworkState.Error(e))
            }
        }
    }

    override suspend fun getAssetGlobalQuote(symbol: String): Flow<NetworkState<AssetGlobalQuoteResponse>> {
        return flow {
            emit(NetworkState.Loading)
            try {
                val response = alphaVantageService.getAssetGlobalQuote(symbol)
                emit(NetworkState.Success(response))
            } catch (e: Exception) {
                emit(NetworkState.Error(e))
            }
        }
    }

}