package com.omouravictor.invest_view.data.repositories

import com.omouravictor.invest_view.data.network.alpha_vantage.AlphaVantageService
import com.omouravictor.invest_view.data.network.alpha_vantage.asset_search.AssetMatchesResponse
import com.omouravictor.invest_view.data.network.base.NetworkResultStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AssetsRepository(private val alphaVantageService: AlphaVantageService) {

    suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkResultStatus<AssetMatchesResponse>> {
        return flow {
            try {
                emit(NetworkResultStatus.Loading)
                emit(NetworkResultStatus.Success(alphaVantageService.getAssetsBySearch(keywords)))
            } catch (e: Exception) {
                emit(NetworkResultStatus.Error("Falha ao buscar os dados na internet :("))
            }
        }
    }
}