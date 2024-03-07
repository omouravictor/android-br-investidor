package com.omouravictor.invest_view.data.network.hgfinanceapi

import com.omouravictor.invest_view.data.network.hgfinanceapi.rates.ApiAssetsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("finance")
    suspend fun getAssets(
        @Query("fields") field: String
    ): ApiAssetsResponse

}