package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkResultState
import com.omouravictor.invest_view.data.network.remote.model.assetsbysearch.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow

interface AssetsRepository {
    suspend fun getRemoteAssetsBySearch(keywords: String): Flow<NetworkResultState<AssetsBySearchResponse>>
}