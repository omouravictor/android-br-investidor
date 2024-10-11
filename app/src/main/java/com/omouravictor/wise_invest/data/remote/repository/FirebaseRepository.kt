package com.omouravictor.wise_invest.data.remote.repository

import com.omouravictor.wise_invest.presenter.user.UserUiModel
import com.omouravictor.wise_invest.presenter.wallet.asset.AssetUiModel

interface FirebaseRepository {
    suspend fun getUser(userId: String): Result<UserUiModel?>
    suspend fun saveUser(userUiModel: UserUiModel): Result<UserUiModel>
    suspend fun getAssetList(userId: String): Result<List<AssetUiModel>>
    suspend fun saveAssetList(userId: String, assetList: List<AssetUiModel>): Result<List<AssetUiModel>>
    suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
}