package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.presenter.wallet.model.AssetUiModel

interface FirebaseRepository {
    suspend fun getAssetList(userId: String): Result<List<AssetUiModel>>
    suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
}