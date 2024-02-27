package com.omouravictor.invest_view.data.repository

import com.omouravictor.invest_view.data.network.base.NetworkResultStatus
import com.omouravictor.invest_view.data.network.hgfinanceapi.ApiService
import com.omouravictor.invest_view.data.network.hgfinanceapi.rates.ApiRatesResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Date

class RatesRepository(
    private val apiService: ApiService
) {

    suspend fun getRemoteRates(fields: String): Flow<NetworkResultStatus<ApiRatesResponse>> =
        flow {
            emit(NetworkResultStatus.Loading)
            try {
                val networkRatesResponse = apiService.getRates(fields)
                    .apply { rateDate = Date() }
                emit(NetworkResultStatus.Success(networkRatesResponse))
            } catch (e: Exception) {
                emit(NetworkResultStatus.Error("Falha ao buscar os dados na internet :("))
            }
        }
}