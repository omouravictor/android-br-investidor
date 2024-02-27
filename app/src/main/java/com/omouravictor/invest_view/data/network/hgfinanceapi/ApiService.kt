package com.omouravictor.invest_view.data.network.hgfinanceapi

import com.omouravictor.invest_view.data.network.hgfinanceapi.rates.ApiRatesResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("finance")
    suspend fun getRates(
        @Query("fields") field: String
    ): ApiRatesResponse

}