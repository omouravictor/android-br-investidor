package com.omouravictor.invest_view.data.network.remote.repository

import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

interface FirebaseRepository {
    suspend fun saveAsset(assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun deleteAsset(assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun getAssetsList(): Result<List<AssetUiModel>>
}