package com.omouravictor.invest_view.data.repository

import com.omouravictor.invest_view.data.remote.alpha_vantage.ApiService
import com.omouravictor.invest_view.data.remote.alpha_vantage.rates.ApiAssetsResponse
import com.omouravictor.invest_view.data.remote.base.RemoteResultStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class AssetsRepository(private val apiService: ApiService) {

    suspend fun getRemoteAssets(fields: String): Flow<RemoteResultStatus<ApiAssetsResponse>> =
        flow {
            emit(RemoteResultStatus.Loading)
            try {
                val networkRatesResponse =
                    apiService.getAssets(fields).apply { rateDate = Date() }
                emit(RemoteResultStatus.Success(networkRatesResponse))
            } catch (e: Exception) {
                emit(RemoteResultStatus.Error("Falha ao buscar os dados na internet :("))
            }
        }
}