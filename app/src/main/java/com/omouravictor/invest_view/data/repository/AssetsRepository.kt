package com.omouravictor.invest_view.data.repository

import com.omouravictor.invest_view.data.network.hgfinanceapi.ApiService
import com.omouravictor.invest_view.data.network.hgfinanceapi.rates.ApiAssetsResponse
import com.omouravictor.invest_view.util.ResultStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class AssetsRepository(
    private val apiService: ApiService
) {

    suspend fun getRemoteAssets(fields: String): Flow<ResultStatus<ApiAssetsResponse>> =
        flow {
            emit(ResultStatus.Loading)
            try {
                val networkRatesResponse =
                    apiService.getAssets(fields).apply { rateDate = Date() }
                emit(ResultStatus.Success(networkRatesResponse))
            } catch (e: Exception) {
                emit(ResultStatus.Error("Falha ao buscar os dados na internet :("))
            }
        }
}