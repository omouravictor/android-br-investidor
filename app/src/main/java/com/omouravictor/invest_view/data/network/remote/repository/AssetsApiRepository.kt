package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.data.network.remote.model.asset_quote.AssetGlobalQuoteResponse
import com.omouravictor.invest_view.data.network.remote.model.assets_by_search.AssetsBySearchResponse
import kotlinx.coroutines.flow.Flow

interface AssetsApiRepository {
    fun getAssetsBySearch(keywords: String): Flow<NetworkState<AssetsBySearchResponse>>
    fun getAssetGlobalQuote(symbol: String): Flow<NetworkState<AssetGlobalQuoteResponse>>
}