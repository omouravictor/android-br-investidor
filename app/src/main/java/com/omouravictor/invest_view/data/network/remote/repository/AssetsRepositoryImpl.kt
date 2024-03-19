package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.model.NetworkState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetsRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    AssetsRepository {

    override suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkState<AssetsBySearchResponse>> {
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