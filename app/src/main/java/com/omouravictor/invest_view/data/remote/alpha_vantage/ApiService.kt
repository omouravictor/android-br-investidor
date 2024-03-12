package com.omouravictor.invest_view.data.remote.alpha_vantage

import com.omouravictor.invest_view.data.remote.alpha_vantage.rates.ApiAssetsResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("finance")
    suspend fun getAssets(
        @Query("fields") field: String
    ): ApiAssetsResponse

}