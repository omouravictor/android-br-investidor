package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import kotlinx.coroutines.flow.Flow

interface RemoteAssetQuoteRepository {
    suspend fun getAssetGlobalQuoteResponse(symbol: String): Flow<NetworkState<AssetGlobalQuoteResponse>>
}