package com.omouravictor.invest_view.data.repositories

import com.omouravictor.invest_view.data.network.alpha_vantage.ApiService
import com.omouravictor.invest_view.data.network.alpha_vantage.rates.ApiAssetsResponse
import com.omouravictor.invest_view.data.network.base.NetworkResultStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class AssetsRepository(private val apiService: ApiService) {

    suspend fun getRemoteAssets(fields: String): Flow<NetworkResultStatus<ApiAssetsResponse>> =
        flow {
            emit(NetworkResultStatus.Loading)
            try {
                val networkRatesResponse =
                    apiService.getAssets(fields).apply { rateDate = Date() }
                emit(NetworkResultStatus.Success(networkRatesResponse))
            } catch (e: Exception) {
                emit(NetworkResultStatus.Error("Falha ao buscar os dados na internet :("))
            }
        }
}