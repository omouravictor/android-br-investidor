package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkResultState
import com.omouravictor.invest_view.data.network.remote.api.AlphaVantageService
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetsRepositoryImpl(private val alphaVantageService: AlphaVantageService) :
    AssetsRepository {

    override suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkResultState<AssetsBySearchResponse>> {
        return flow {
            emit(NetworkResultState.Loading)
            try {
                val response = alphaVantageService.getAssetsBySearch(keywords)
                emit(NetworkResultState.Success(response))
            } catch (e: Exception) {
                emit(NetworkResultState.Error(e))
            }
        }
    }
}