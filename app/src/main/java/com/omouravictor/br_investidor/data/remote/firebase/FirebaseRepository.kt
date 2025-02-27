package com.omouravictor.br_investidor.data.remote.firebase

import com.omouravictor.br_investidor.presenter.user.UserUiModel
import com.omouravictor.br_investidor.presenter.wallet.asset.AssetUiModel

interface FirebaseRepository {
    suspend fun getUser(userId: String): Result<UserUiModel?>
    suspend fun saveUser(userUiModel: UserUiModel): Result<UserUiModel>
    suspend fun deleteUser(userUiModel: UserUiModel): Result<UserUiModel>
    suspend fun getAssetList(userId: String): Result<List<AssetUiModel>>
    suspend fun saveAssetList(userId: String, assetList: List<AssetUiModel>): Result<List<AssetUiModel>>
    suspend fun saveAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
    suspend fun deleteAsset(userId: String, assetUiModel: AssetUiModel): Result<AssetUiModel>
}