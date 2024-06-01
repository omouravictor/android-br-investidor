package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.data.network.base.NetworkState
import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    fun saveAsset(assetUiModel: AssetUiModel): Flow<NetworkState<AssetUiModel>>
}