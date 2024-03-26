package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.assetquote.AssetQuoteResponse
import kotlinx.coroutines.flow.Flow

interface AssetQuoteRepository {
    suspend fun getRemoteAssetQuote(symbol: String): Flow<NetworkState<AssetQuoteResponse>>
}