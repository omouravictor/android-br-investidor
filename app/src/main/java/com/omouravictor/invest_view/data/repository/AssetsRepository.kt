package com.omouravictor.invest_view.data.repository

import com.omouravictor.invest_view.data.network.alpha_vantage.AlphaVantageService
import com.omouravictor.invest_view.data.network.alpha_vantage.model.asset_search.AssetMatchesResponse
import com.omouravictor.invest_view.data.network.base.NetworkResultState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetsRepository(private val alphaVantageService: AlphaVantageService) {

    suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkResultState<AssetMatchesResponse>> {
        return flow {
            try {
                emit(NetworkResultState.Loading)
                emit(NetworkResultState.Success(alphaVantageService.getAssetsBySearch(keywords)))
            } catch (e: Exception) {
                emit(NetworkResultState.Error("Falha ao buscar os dados na internet :("))
            }
        }
    }
}