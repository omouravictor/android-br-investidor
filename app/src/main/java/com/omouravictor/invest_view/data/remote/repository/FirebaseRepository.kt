package com.omouravictor.invest_view.data.remote.repository

import com.omouravictor.invest_view.presenter.user.UserUiModel
import com.omouravictor.invest_view.presenter.wallet.asset.AssetUiModel

interface FirebaseRepository {
    suspend fun getUser(userId: String): Result<UserUiModel?>
    suspend fun saveUser(userUiModel: UserUiModel): Result<UserUiModel>
    suspend fun getAssetList(userId: String): Result<List<AssetUiModel>>
    suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
}