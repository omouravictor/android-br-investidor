package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow

interface AssetsBySearchRepository {
    suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkState<AssetsBySearchResponse>>
}