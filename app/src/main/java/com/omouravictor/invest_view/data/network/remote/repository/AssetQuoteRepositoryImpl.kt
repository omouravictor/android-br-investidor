package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.assetquote.AssetQuoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetQuoteRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    AssetQuoteRepository {

    override suspend fun getRemoteAssetQuote(symbol: String): Flow<NetworkState<AssetQuoteResponse>> {
        return flow {
            emit(NetworkState.Loading)
            try {
                val response = alphaVantageService.getAssetQuote(symbol)
                emit(NetworkState.Success(response))
            } catch (e: Exception) {
                emit(NetworkState.Error(e))
            }
        }
    }
}