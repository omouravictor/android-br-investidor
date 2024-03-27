package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class RemoteAssetsBySearchRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    RemoteAssetsBySearchRepository {

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
}