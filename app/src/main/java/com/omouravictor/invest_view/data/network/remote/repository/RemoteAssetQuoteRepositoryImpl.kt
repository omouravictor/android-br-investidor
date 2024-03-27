package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteAssetQuoteRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    RemoteAssetQuoteRepository {

    override suspend fun getAssetGlobalQuoteResponse(symbol: String): Flow<NetworkState<AssetGlobalQuoteResponse>> {
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